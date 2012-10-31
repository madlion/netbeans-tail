package de.fishheadsoftware.netbeanstail;

import static de.fishheadsoftware.netbeanstail.Bundle.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.InputOutput;

/**
 *
 * @author madlion
 */
@Messages("ClearOutut=Clear Output")
public class ClearOutputAction extends AbstractAction {
    
    private InputOutput inputOutput;
    
    public ClearOutputAction() {
        putValue(Action.SMALL_ICON, ImageUtilities.loadImageIcon("de/fishheadsoftware/netbeanstail/edit-clear.png", false));
        putValue(Action.SHORT_DESCRIPTION, ClearOutut());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (inputOutput != null) {
                inputOutput.getOut().reset();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void setInputOutput(final InputOutput inputOutput) {
        this.inputOutput = inputOutput;
    }
}
