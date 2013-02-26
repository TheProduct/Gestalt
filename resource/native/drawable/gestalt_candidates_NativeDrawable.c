#include <OpenGL/gl.h>
#include "gestalt_candidates_NativeDrawable.h"

JNIEXPORT void JNICALL Java_gestalt_candidates_NativeDrawable_init
  (JNIEnv *env, jobject obj) {
    //printf("init.\n");
}

JNIEXPORT void JNICALL Java_gestalt_candidates_NativeDrawable_draw
  (JNIEnv *env, jobject obj) {
    //printf("draw.\n");

	/* write something nasty in here */
    
    glPushMatrix();
	glTranslatef(-100, 0, 0);
    glScalef(100, 100, 100);

	glColor3f(1, 1, 0);
	
	glBegin(GL_QUADS);
	
	glNormal3f(0, 0, 1);
	
	glTexCoord2f(0, 0);
	glVertex2f(0, 0);
	
	glTexCoord2f(1, 0);
	glVertex2f(1, 0);
	
	glTexCoord2f(1, 1);
	glVertex2f(1, 1);
	
	glTexCoord2f(0, 1);
	glVertex2f(0, 1);
	
	glEnd();
	
    glPopMatrix();
}