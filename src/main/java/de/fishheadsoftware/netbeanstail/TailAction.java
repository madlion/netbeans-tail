package de.fishheadsoftware.netbeanstail;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle.Messages;

@ActionID(
    category = "Tools",
id = "de.fishheadsoftware.netbeanstail.TailAction")
@ActionRegistration(
    displayName = "#CTL_TailAction")
@ActionReference(path = "Menu/Tools", position = 1800, separatorBefore = 1750)
@Messages("CTL_TailAction=Tail")
public final class TailAction implements ActionListener, ScheduleListener {
    
    private final Map<File, TailRunnable> files;
    
    public TailAction() {
        files = new ConcurrentHashMap<File, TailRunnable>();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                performAction();
            }
        });
    }
    
    private void performAction() {
        final FileChooserBuilder builder = new FileChooserBuilder(TailAction.class);
        final File file = builder.setFilesOnly(true).showOpenDialog();
        if (file != null) {
            final TailRunnable tailRunnable;
            if (files.containsKey(file)) {
                tailRunnable = files.get(file);
                tailRunnable.select();
            } else {
                tailRunnable = new TailRunnable(file);
                tailRunnable.addScheduleListener(this);
                final ScheduledFuture<?> scheduleHandle = Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(tailRunnable, 0, 1000, TimeUnit.MILLISECONDS);
                tailRunnable.setHandle(scheduleHandle);
                files.put(file, tailRunnable);
            }
        }
    }

    @Override
    public void cancelled(TailRunnable runnable) {
        runnable.removeScheduleListener(this);
        files.remove(runnable.getFile());
    }
}
