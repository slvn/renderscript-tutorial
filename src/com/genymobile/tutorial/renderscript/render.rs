#pragma version(1)
#pragma rs java_package_name(com.genymobile.tutorial.renderscript)

#include "rs_graphics.rsh"

rs_program_vertex programVertex;
rs_program_fragment programFragment;
rs_program_raster programRaster;
rs_program_store programStore;

rs_allocation texture;

// Parameters 
bool debug;
int width;
int height;

// Internal parameters
float rotation;
float last_time;
int frame;
int fps;


void init() {
	rotation = 0.0f;
	last_time = rsUptimeMillis();
	frame = 0;
}

void printFPS(){
	if ((rsUptimeMillis() - last_time)  > 1000) {
		last_time = rsUptimeMillis();
		fps = frame;
		frame = 0;
		rsDebug("FPS", fps);
	}
	frame++;
}


int root() {

	rsgClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Clear background 

	float startX = -width/2, startY = - height/2;
	
	// Chargement du Vertex Shader
    rsgBindProgramVertex(programVertex);
    
    // Mise en place de la matrice de projection
    rs_matrix4x4 projection;
    rsMatrixLoadOrtho(&projection, 0, rsgGetWidth(), rsgGetHeight(), 0, -width/2, width/2);
    rsgProgramVertexLoadProjectionMatrix(&projection);
    
    rsgBindProgramFragment(programFragment);
    rsgBindTexture(programFragment, 0, texture);
    rsgBindProgramStore(programStore);
	rsgBindProgramRaster(programRaster);
	
	printFPS();
	
	rs_matrix4x4 matrix;
    rsMatrixLoadIdentity(&matrix);
    rsMatrixTranslate(&matrix, width/2, height/2, 0.0f);
   	rsMatrixRotate(&matrix, rotation++, 0.0f, 1.0f, 0.0f);

    rsgProgramVertexLoadModelMatrix(&matrix);
       
    rsgDrawQuadTexCoords(startX, startY, 0, 0, 0,
                         startX, startY + height, 0, 0, 1,
                         startX + width, startY + height, 0, 1, 1,
                         startX + width, startY, 0, 1, 0);

	return 16; 
}

