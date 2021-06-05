package userapp;

import database.ProtectedDataManipulator;

public abstract class AppController {
    
    protected Launcher launcher;
    protected ProtectedDataManipulator dataManipulator;
    
    public void setCrossing(Launcher launcher, ProtectedDataManipulator dataManipulator) {
        this.launcher = launcher;
        this.dataManipulator = dataManipulator;
    }

    public void localeChangeEvent() {
        
    }
}
