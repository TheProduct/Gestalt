#include <OpenGL/gl.h>
#include <stdio.h>
#include <OpenGL/glu.h>
#include <stdlib.h>
#include <stdbool.h>

#include <ffmpeg/avcodec.h>
#include <ffmpeg/avformat.h>

#include "gestalt_candidates_NativeMovieTextureProducer.h"

typedef unsigned char byte;

/* Is it the first call to the store texture function */
bool _myIsInitialized = false;

/* The used texture id */
int _myOpenGLTextureID;

/* Is a frame prepared and ready? */
bool _myNewFrameIsReady = false;

/* Declarations GL Side */
GLuint texture;
void storeTexture(  int iFrame);



/* Declarations ffmpeg Side */
AVFormatContext *pFormatCtx;
int             i, videoStream;
AVCodecContext  *pCodecCtx;
AVCodec         *pCodec;
AVFrame         *pFrame; 
AVFrame         *pFrameRGB;
AVPacket        packet;
int             frameFinished;
int             numBytes;
uint8_t         *buffer;

int 			width;
int				height;


/* Prepare all the stuff */
JNIEXPORT void JNICALL Java_gestalt_candidates_NativeMovieTextureProducer_init
(JNIEnv *env, jobject obj, jstring theFileName, jint theOpenGLTextureID){
	fprintf(stderr, "INIT \n");

	/* Convert the jstring to char array */
	const char *str = (*env)->GetStringUTFChars(env, theFileName, 0);


	/* Store texture ID */
	_myOpenGLTextureID = theOpenGLTextureID;


	/* Register all formats and codecs */
	av_register_all();

	if(av_open_input_file(&pFormatCtx, str, NULL, 0, NULL)!=0)
		fprintf(stderr, "Could not open file!\n");

	if(av_find_stream_info(pFormatCtx)<0)
		fprintf(stderr, "Couldn't find stream info!\n");

	dump_format(pFormatCtx, 0,str, 0);

	/* Find first stream */
	videoStream=-1;
	for(i=0; i<pFormatCtx->nb_streams; i++)
	if(pFormatCtx->streams[i]->codec->codec_type==CODEC_TYPE_VIDEO) {
		videoStream=i;
		break;
	}
	if(videoStream==-1)
		fprintf(stderr, "Could not find video stream!\n");

	/* et pointer to codec context of stream */
	pCodecCtx=pFormatCtx->streams[videoStream]->codec;

	/* Find the decoder for the video stream */
	pCodec=avcodec_find_decoder(pCodecCtx->codec_id);
	if(pCodec==NULL) {
		fprintf(stderr, "Unsupported codec!\n");
	}
	/* Open codec */
	if(avcodec_open(pCodecCtx, pCodec)<0)
		fprintf(stderr, "Could not open codec!\n");

	/* Allocate video frame */
	pFrame=avcodec_alloc_frame();

	/* Allocate an AVFrame structure */
	pFrameRGB=avcodec_alloc_frame();
	if(pFrameRGB==NULL)
		fprintf(stderr, "Frame structure somehow fucked up!\n");

	/* Determine required buffer size and allocate buffer */
	numBytes=avpicture_get_size(PIX_FMT_RGB24, pCodecCtx->width,
		pCodecCtx->height);
	buffer=(uint8_t *)av_malloc(numBytes*sizeof(uint8_t));

	/* Fill AVPicture */
	avpicture_fill((AVPicture *)pFrameRGB, buffer, PIX_FMT_RGB24,
		pCodecCtx->width, pCodecCtx->height);


	width =	pCodecCtx->width;
	height = pCodecCtx->height;

	/* Important: release strings */
	(*env)->ReleaseStringUTFChars(env, theFileName, str);




}


/* Store texture to OpenGL */
void storeTexture(int iFrame) {
	int  y;
	int  x;

	glBindTexture( GL_TEXTURE_RECTANGLE_ARB, _myOpenGLTextureID );

	glTexParameteri(GL_TEXTURE_RECTANGLE_ARB,GL_TEXTURE_MIN_FILTER,GL_LINEAR);	// Linear Filtering
	glTexParameteri(GL_TEXTURE_RECTANGLE_ARB,GL_TEXTURE_MAG_FILTER,GL_LINEAR);	// Linear Filtering

	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

	/* Init the texture with glTexImage2D */
	if(_myIsInitialized == false) {
		glTexImage2D  (GL_TEXTURE_RECTANGLE_ARB, 0, GL_RGB, width , height, 0, GL_RGB, GL_UNSIGNED_BYTE, pFrameRGB->data[0]);
		glCopyTexImage2D(GL_TEXTURE_RECTANGLE_ARB,0,GL_RGB,0,0,width,height,0);

		_myIsInitialized = true;
		
	}

	glTexSubImage2D(GL_TEXTURE_RECTANGLE_ARB, 0, 0, 0, width, height, GL_RGB, GL_UNSIGNED_BYTE, pFrameRGB->data[0]);
}



/* Pass the Texture ID back to Java */
JNIEXPORT jint JNICALL Java_gestalt_candidates_NativeMovieTextureProducer_requestOpenGlTextureId
(JNIEnv *env, jobject obj){
	return (int)_myOpenGLTextureID;
}


JNIEXPORT jint JNICALL Java_gestalt_candidates_NativeMovieTextureProducer_getWidth
(JNIEnv *env, jobject obj){
	return (jint)width;
}


JNIEXPORT jint JNICALL Java_gestalt_candidates_NativeMovieTextureProducer_getHeight
(JNIEnv *env, jobject obj){
	return (jint)height;
}


JNIEXPORT void JNICALL Java_gestalt_candidates_NativeMovieTextureProducer_tryToReadNewFrame
	(JNIEnv *env, jobject obj)
{
	if(!_myNewFrameIsReady){

		if(av_read_frame(pFormatCtx, &packet)>=0) {
			/* Is this a packet from the video stream? */
			if(packet.stream_index==videoStream) {
				/* Decode video frame */
				avcodec_decode_video(pCodecCtx, pFrame, &frameFinished, 
					packet.data, packet.size);

				/* Is the frame ready? */
				if(frameFinished) {

					/* Convert the image from its native format to RGB */
					img_convert((AVPicture *)pFrameRGB, PIX_FMT_RGB24, 
						(AVPicture*)pFrame, pCodecCtx->pix_fmt, pCodecCtx->width, 
						pCodecCtx->height);

					_myNewFrameIsReady = true;
				}
			}
		}
		else {
			// Do nothing
		}
	}
}



JNIEXPORT void JNICALL Java_gestalt_candidates_NativeMovieTextureProducer_update
(JNIEnv *env, jobject obj){
	

	if(_myNewFrameIsReady){
		
		storeTexture(i);

		// Free the packet that was allocated by av_read_frame
		av_free_packet(&packet);

		_myNewFrameIsReady = false;
	} else {

	}



}



// At thermination: DO THIS DO THIS


// // Free the RGB image
// av_free(buffer);
// av_free(pFrameRGB);
//
// // Free the YUV frame
// av_free(pFrame);
//
// // Close the codec
// avcodec_close(pCodecCtx);
//
// // Close the video file
// av_close_input_file(pFormatCtx);
//





