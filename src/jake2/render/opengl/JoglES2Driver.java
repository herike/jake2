/*
 * JoglDriver.java
 * Copyright (C) 2004
 * 
 */
/*
Copyright (C) 1997-2001 Id Software, Inc.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 */

package jake2.render.opengl;

import jake2.qcommon.xcommand_t;
import jake2.render.Base;

import javax.media.nativewindow.util.Dimension;
import javax.media.opengl.*;

import com.jogamp.newt.ScreenMode;

/**
 * JoglCommon
 */
public abstract class JoglES2Driver extends JoglES2 implements GLDriver {

    protected JoglES2Driver() {
        // singleton
    }

    private NEWTWin newtWin = null;

    public ScreenMode[] getModeList() {
        if(null == newtWin) {
            throw new RuntimeException("NEWTWin not yet initialized.");
        }
        return newtWin.getModeList();        
    }
    
    public int setMode(Dimension dim, int mode, boolean fullscreen) {
        if(null == newtWin) {
            newtWin = new NEWTWin();
        }
        int res = newtWin.setMode(dim, mode, fullscreen);
        if( Base.rserr_ok == res ) {
            
            setGL(newtWin.window.getGL().getGL2());
            init(0, 0);
            
            return Base.rserr_ok;
        }
        return res;
    }

    public void shutdown() {
        if(null != newtWin) {
            newtWin.shutdown();
        }
    }

    /**
     * @return true
     */
    public boolean init(int xpos, int ypos) {
        // clear the screen
        // first buffer
        beginFrame(0.0f);
        glViewport(0, 0, newtWin.window.getWidth(), newtWin.window.getHeight());
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        endFrame();
        // second buffer
        beginFrame(0.0f);
        glViewport(0, 0, newtWin.window.getWidth(), newtWin.window.getHeight());
        glClearColor(0, 0, 0, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        endFrame();
        return true;
    }

    public void beginFrame(float camera_separation) {
        activate();
    }

    public void endFrame() {
        final GLContext ctx = newtWin.window.getContext();
        if ( null != ctx && GLContext.getCurrent() == ctx) {
            ctx.release();
        }
        newtWin.window.swapBuffers();
        newtWin.fpsCounter.tickFPS();
    }

    public void appActivate(boolean activate) {
        // do nothing
    }

    public void enableLogging(boolean enable) {
        // do nothing
    }

    public void logNewFrame() {
        // do nothing
    }

    /*
     * @see jake2.client.refexport_t#updateScreen()
     */
    public void updateScreen(xcommand_t callback) {
        callback.execute();
    }

    protected void activate() {
        final GLContext ctx = newtWin.window.getContext();
        if ( null != ctx && GLContext.getCurrent() != ctx ) {                
            ctx.makeCurrent();
        }
    }

    // --------------------------------------------------------------------------    
}