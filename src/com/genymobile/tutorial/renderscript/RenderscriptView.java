
package com.genymobile.tutorial.renderscript;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.ProgramFragmentFixedFunction;
import android.renderscript.ProgramRaster;
import android.renderscript.ProgramStore;
import android.renderscript.ProgramVertexFixedFunction;
import android.renderscript.RSTextureView;
import android.renderscript.RenderScriptGL;
import android.util.AttributeSet;

public class RenderscriptView extends RSTextureView {

    // Attributs du scripts
    private RenderScriptGL mRs;
    private ScriptC_render mScript;

    private Allocation mTexture;

    public RenderscriptView(Context context) {
        super(context);
    }
    
    public RenderscriptView(Context context, AttributeSet set) {
        super(context, set);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        
        // Chargement du script
        RenderScriptGL.SurfaceConfig sc = new RenderScriptGL.SurfaceConfig();
        mRs = createRenderScriptGL(sc);
        mScript = new ScriptC_render(mRs, getResources(), R.raw.render);

        // Récupération de l'image
        loadBitmap(getResources(), R.drawable.logo);
        
        // Initialisation des différents objets nécessaire au rendu 3D
        initProgramVertex();
        initProgramFragment();
        initProgramRaster();
        initProgramStore();

        mScript.set_debug(true);
        mRs.bindRootScript(mScript);
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        
        if (mRs != null) {
            mRs.destroy();
            mRs = null;
        }
    }

    private void loadBitmap(Resources res, int id) {
        // Récupération de l'image
        Bitmap b = BitmapFactory.decodeResource(res, id);

        // Récupération d'une Allocation contenant la texture
        mTexture = Allocation.createFromBitmap(mRs, b,
                Allocation.MipmapControl.MIPMAP_ON_SYNC_TO_TEXTURE,
                Allocation.USAGE_GRAPHICS_TEXTURE);
        
        // Configuration du script
        mScript.set_height(b.getHeight());
        mScript.set_width(b.getWidth());
        mScript.set_texture(mTexture);
    }

    private void initProgramFragment() {
        // Gestion par defaut des couleurs des pixels
        ProgramFragmentFixedFunction.Builder pgBuilder = new ProgramFragmentFixedFunction.Builder(mRs);
        // Force l'utilisation de la transparence (alpha)
        pgBuilder.setTexture(ProgramFragmentFixedFunction.Builder.EnvMode.REPLACE,
                ProgramFragmentFixedFunction.Builder.Format.RGBA, 0);
        mScript.set_programFragment(pgBuilder.create());
    }

    private void initProgramVertex() {
        // Gestion par defaut des vertices
        mRs.bindProgramVertex(new ProgramVertexFixedFunction.Builder(mRs).create());
    }
    
    private void initProgramStore() {
        // Gestion par defaut de la transparence
        mScript.set_programStore(ProgramStore.BLEND_ALPHA_DEPTH_NONE(mRs));
    }

    private void initProgramRaster() {
        // Pas de Culling (la texture s'affiche des deux cotés)
        mScript.set_programRaster(ProgramRaster.CULL_NONE(mRs));
    }
}
