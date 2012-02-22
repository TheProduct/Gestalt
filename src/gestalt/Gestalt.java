/*
 * Gestalt
 *
 * Copyright (C) 2012 Patrick Kochlik + Dennis Paul
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */


package gestalt;


/**
 * the collection of constants used in gestalt.
 */
public interface Gestalt {

    /**
     *
     */
    boolean MACOSX = System.getProperty("os.name").equals("Mac OS X");

    /** @todo this must be unified in one property */
    boolean INTEL = System.getProperty("os.arch").equals("i386");

    /**
     *
     */
    int UNDEFINED = -1;

    /**
     *
     */
    int HINT_LEAVE_STATE_UNCHANGED = -2;

    /* mathematik */
    /**
     *
     */
    float PI = (float)Math.PI;

    /**
     *
     */
    float PI_HALF = (float)Math.PI * 0.5f;

    /**
     *
     */
    float TWO_PI = (float)Math.PI * 2.0f;

    /**
     *
     */
    float SQRT_TWO = (float)Math.sqrt(2);

    /**
     *
     */
    float EPSILON = 0.0001f;

    /**
     *
     */
    int RED = 0;

    /**
     *
     */
    int GREEN = 1;

    /**
     *
     */
    int BLUE = 2;

    /**
     *
     */
    int ALPHA = 3;

    /* render engines */
    /**
     *
     */
    int ENGINE_JOGL = 0;

    /* texture */
    /**
     *
     */
    int BITMAP_COMPONENT_ORDER_BGRA = 10;

    /**
     *
     */
    int BITMAP_COMPONENT_ORDER_RGBA = 11;

    /**
     *
     */
    int BITMAP_BLENDMODE_OVERWRITE = 0;

    /**
     *
     */
    int BITMAP_BLENDMODE_ADD = 1;

    /**
     *
     */
    int BITMAP_BLENDMODE_MULTIPLY = 2;

    /**
     *
     */
    int BITMAP_BLENDMODE_ADD_2 = 3;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_ZERO = 0;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_ONE = 1;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_DST_COLOR = 2;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_SRC_COLOR = 3;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_ONE_MINUS_DST_COLOR = 4;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_ONE_MINUS_SRC_COLOR = 5;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_SRC_ALPHA = 6;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_ONE_MINUS_SRC_ALPHA = 7;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_DST_ALPHA = 8;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_ONE_MINUS_DST_ALPHA = 9;

    /**
     *
     */
    int BITMAP_BLENDFACTOR_SRC_ALPHA_SATURATE = 10;

    /**
     *
     */
    int TEXTURE_MEDIA_IMAGE_BGR = 12;

    /**
     *
     */
    int TEXTURE_PROPORTION_POWEROF2 = 13;

    /**
     *
     */
    int TEXTURE_PROPORTION_ARBITRARY = 14;

    /**
     *
     */
    int TEXTURE_FILTERTYPE_NEAREST = 17;

    /**
     *
     */
    int TEXTURE_FILTERTYPE_LINEAR = 18;

    /**
     *
     */
    int TEXTURE_FILTERTYPE_MIPMAP = 19;

    /**
     *
     */
    int TEXTURE_WRAPMODE_CLAMP = 20;

    /**
     *
     */
    int TEXTURE_WRAPMODE_REPEAT = 21;

    /**
     *
     */
    int TEXTURE_WRAPMODE_CLAMP_TO_BORDER = 22;

    /* fog filter */
    /**
     *
     */
    int FOG_FILTER_EXP = 0;

    /**
     *
     */
    int FOG_FILTER_EXP2 = 1;

    /**
     *
     */
    int FOG_FILTER_LINEAR = 2;

    /* material */
    /**
     *
     */
    int MATERIAL_BLEND_ALPHA = 30;

    /**
     *
     */
    int MATERIAL_BLEND_INVERS_MULTIPLY = 31;

    /**
     *
     */
    int MATERIAL_BLEND_WHITE_INVERT = 32;

    /**
     *
     */
    int MATERIAL_BLEND_BRIGHTER = 33;

    /**
     *
     */
    int MATERIAL_BLEND_MULTIPLY = 34;

    /**
     *
     */
    int MATERIAL_BLEND_CUSTOM = 35;

    /**
     *
     */
    int MATERIAL_NORMAL_NORMALIZE = 50;

    /**
     *
     */
    int MATERIAL_NORMAL_RESCALE_NORMALS = 51;

    /* camera */
    /**
     *
     */
    int CAMERA_MODE_ROTATE_XYZ = 40;

    /**
     *
     */
    int CAMERA_MODE_LOOK_AT = 41;

    /**
     *
     */
    int CAMERA_MODE_ROTATION_AXIS = 42;

    /**
     *
     */
    float CAMERA_A_HANDY_ANGLE = 53.130102354156f;

    float CAMERA_A_HANDY_ANGLE_FOVX = 67.38013662815706f;

    /**
     *
     */
    int CAMERA_CULLING_BACKFACE = 0;

    /**
     *
     */
    int CAMERA_CULLING_FRONTFACE = 1;

    /**
     *
     */
    int CAMERA_CULLING_FRONT_AND_BACKFACE = 2;

    /**
     *
     */
    int CAMERA_CULLING_NONE = 3;

    /* shape */
    /**
     *
     */
    int SHAPE_ORIGIN_BOTTOM_LEFT = 53;

    /**
     *
     */
    int SHAPE_ORIGIN_BOTTOM_RIGHT = 54;

    /**
     *
     */
    int SHAPE_ORIGIN_TOP_LEFT = 55;

    /**
     *
     */
    int SHAPE_ORIGIN_TOP_RIGHT = 56;

    /**
     *
     */
    int SHAPE_ORIGIN_CENTERED = 57;

    /**
     *
     */
    int SHAPE_ORIGIN_CENTERED_LEFT = 158;

    /**
     *
     */
    int SHAPE_ORIGIN_CENTERED_RIGHT = 159;

    /**
     *
     */
    int SHAPE_ORIGIN_TOP_CENTERED = 160;

    /**
     *
     */
    int SHAPE_ORIGIN_BOTTOM_CENTERED = 161;

    /**
     *
     */
    int SHAPE_TRANSFORM_MATRIX = 58;

    /**
     *
     */
    int SHAPE_TRANSFORM_POSITION_AND_ROTATION = 59;

    /**
     *
     */
    int SHAPE_TRANSFORM_MATRIX_AND_ROTATION = 60;

    /**
     *
     */
    int SHAPE_CUBE_TEXTURE_SAME_FOR_EACH_SIDE = 61;

    /**
     *
     */
    int SHAPE_CUBE_TEXTURE_WRAP_AROUND = 62;

    /* default renderbins */
    /**
     *
     */
    int BIN_FRAME_SETUP = 0;

    /**
     *
     */
    int BIN_2D_BACKGROUND_SETUP = 1;

    /**
     *
     */
    int BIN_2D_BACKGROUND = 2;

    /**
     *
     */
    int BIN_2D_BACKGROUND_FINISH = 3;

    /**
     *
     */
    int BIN_3D_SETUP = 4;

    /**
     *
     */
    int BIN_3D = 5;

    /**
     *
     */
    int BIN_3D_FINISH = 6;

    /**
     *
     */
    int BIN_2D_FOREGROUND_SETUP = 7;

    /**
     *
     */
    int BIN_2D_FOREGROUND = 8;

    /**
     *
     */
    int BIN_2D_FOREGROUND_FINISH = 9;

    /**
     *
     */
    int BIN_ARBITRARY = 10;

    /**
     *
     */
    int BIN_FRAME_FINISH = 11;

    /**
     *
     */
    int BIN_NUMBER_OF_DEFAULT_BINS = 12;

    /* shapebin */
    /**
     *
     */
    int SHAPEBIN_SORT_BY_Z_POSITION = 0;

    /**
     *
     */
    int SHAPEBIN_SORT_BY_DISTANCE_TO_CAMERA = 1;

    /**
     *
     */
    int SHAPEBIN_SORT_BY_Z_DISTANCE_TO_CAMERAPLANE = 2;

    /* pickingbins */
    /**
     *
     */
    int PICKING_BIN_3D = 0;

    /**
     *
     */
    int PICKING_BIN_2D = 1;

    /* font */
    /**
     *
     */
    int FONT_QUALITY_LOW = 0;

    /**
     *
     */
    int FONT_QUALITY_HIGH = 1;

    /* keys */
    /**
     *
     */
    int KEYCODE_ESCAPE = 1000;

    /**
     *
     */
    int KEYCODE_LEFT = 1001;

    /**
     *
     */
    int KEYCODE_RIGHT = 1002;

    /**
     *
     */
    int KEYCODE_UP = 1003;

    /**
     *
     */
    int KEYCODE_DOWN = 1004;

    /**
     *
     */
    int KEYCODE_SHIFT = 1005;

    /**
     *
     */
    int KEYCODE_A = 1006;

    /**
     *
     */
    int KEYCODE_B = 1007;

    /**
     *
     */
    int KEYCODE_C = 1008;

    /**
     *
     */
    int KEYCODE_D = 1009;

    /**
     *
     */
    int KEYCODE_E = 1010;

    /**
     *
     */
    int KEYCODE_F = 1011;

    /**
     *
     */
    int KEYCODE_G = 1012;

    /**
     *
     */
    int KEYCODE_H = 1013;

    /**
     *
     */
    int KEYCODE_I = 1014;

    /**
     *
     */
    int KEYCODE_J = 1015;

    /**
     *
     */
    int KEYCODE_K = 1016;

    /**
     *
     */
    int KEYCODE_L = 1017;

    /**
     *
     */
    int KEYCODE_M = 1018;

    /**
     *
     */
    int KEYCODE_N = 1019;

    /**
     *
     */
    int KEYCODE_O = 1020;

    /**
     *
     */
    int KEYCODE_P = 1021;

    /**
     *
     */
    int KEYCODE_Q = 1022;

    /**
     *
     */
    int KEYCODE_R = 1023;

    /**
     *
     */
    int KEYCODE_S = 1024;

    /**
     *
     */
    int KEYCODE_T = 1025;

    /**
     *
     */
    int KEYCODE_U = 1026;

    /**
     *
     */
    int KEYCODE_V = 1027;

    /**
     *
     */
    int KEYCODE_W = 1028;

    /**
     *
     */
    int KEYCODE_X = 1029;

    /**
     *
     */
    int KEYCODE_Y = 1030;

    /**
     *
     */
    int KEYCODE_Z = 1031;

    /**
     *
     */
    int KEYCODE_PAGE_UP = 1032;

    /**
     *
     */
    int KEYCODE_PAGE_DOWN = 1033;

    /**
     *
     */
    int KEYCODE_META = 1034;

    /**
     *
     */
    int KEYCODE_SPACE = 1035;

    /**
     *
     */
    int KEYCODE_DEL = 8;

    /**
     *
     */
    int KEYCODE_0 = 1036;

    /**
     *
     */
    int KEYCODE_1 = 1037;

    /**
     *
     */
    int KEYCODE_2 = 1038;

    /**
     *
     */
    int KEYCODE_3 = 1039;

    /**
     *
     */
    int KEYCODE_4 = 1040;

    /**
     *
     */
    int KEYCODE_5 = 1041;

    /**
     *
     */
    int KEYCODE_6 = 1042;

    /**
     *
     */
    int KEYCODE_7 = 1043;

    /**
     *
     */
    int KEYCODE_8 = 1044;

    /**
     *
     */
    int KEYCODE_9 = 1045;

    /**
     *
     */
    int MOUSEBUTTON_LEFT = 0;

    /**
     *
     */
    int MOUSEBUTTON_MIDDLE = 1;

    /**
     *
     */
    int MOUSEBUTTON_RIGHT = 2;

    /* image formats */
    /**
     *
     */
    int IMAGE_FILEFORMAT_JPEG = 10;

    /**
     *
     */
    int IMAGE_FILEFORMAT_TGA = 13;

    /**
     *
     */
    int IMAGE_FILEFORMAT_PNG = 14;

    /* font */
    /**
     *
     */
    int FONT_ALIGN_LEFT = 0;

    /**
     *
     */
    int FONT_ALIGN_CENTER = 1;

    /**
     *
     */
    int FONT_ALIGN_RIGHT = 2;

    /**
     *
     */
    int FONT_STYLE_REGULAR = 0;

    /**
     *
     */
    int FONT_STYLE_BOLD = 1;

    /**
     *
     */
    int FONT_STYLE_ITALIC = 2;

    /* mesh */
    /**
     *
     */
    int MESH_TRIANGLES = 0;

    /**
     *
     */
    int MESH_QUADS = 1;

    /**
     *
     */
    int MESH_QUAD_STRIP = 2;

    /**
     *
     */
    int MESH_POINTS = 3;

    /**
     *
     */
    int MESH_LINES = 4;

    /**
     *
     */
    int MESH_LINE_LOOP = 5;

    /**
     *
     */
    int MESH_POLYGON = 6;

    /**
     * 
     */
    int MESH_TRIANGLE_STRIP = 7;

    /**
     * 
     */
    int MESH_TRIANGLE_FAN = 8;

    /* line */
    /**
     *
     */
    int LINE_PRIMITIVE_TYPE_LINES = 4;

    /**
     *
     */
    int LINE_PRIMITIVE_TYPE_LINE_LOOP = 5;

    /**
     *
     */
    int LINE_PRIMITIVE_TYPE_LINE_STRIP = 6;

    /* movie */
    /**
     *
     */
    int MOVIE_DIRECTION_FORWARD = 1;

    /**
     * 
     */
    int MOVIE_DIRECTION_BACKWARDS = -1;
}
