package de.fishheadsoftware.netbeanstail;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import javax.swing.Action;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.IOTab;
import org.openide.windows.InputOutput;

/**
 *
 * @author madlion
 */
public class TailRunnable implements Runnable {

    private final File file;
    private long filePointer;
    private final InputOutput inputOutput;
    private final List<ScheduleListener> listeners;
    private ScheduledFuture<?> scheduleHandle;

    public TailRunnable(File file) {
        this.file = file;
        filePointer = 0;
        listeners = new ArrayList<ScheduleListener>();
        inputOutput = IOProvider.getDefault().getIO(String.format("Tail - %s", file.getName()), new Action[0]);
        IOTab.setToolTipText(inputOutput, String.format("Tail - %s", file.getAbsoluteFile()));
        inputOutput.select();
    }

    @Override
    public void run() {
        if (inputOutput.isClosed()) {
            scheduleHandle.cancel(true);
            notifyListeners();
        } else {
            long fileLength = file.length();

            if (fileLength < filePointer) {
                filePointer = fileLength;
            } else if (fileLength > filePointer) {
                RandomAccessFile randomAccessFile = null;
                try {
                    randomAccessFile = new RandomAccessFile(file, "r");
                    randomAccessFile.seek(filePointer);
                    String line;
                    while ((line = randomAccessFile.readLine()) != null) {
                        inputOutput.getOut().println(line);
                    }
                    filePointer = randomAccessFile.getFilePointer();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        }
    }

    public void select() {
        inputOutput.select();
    }

    public void setHandle(final ScheduledFuture<?> scheduleHandle) {
        this.scheduleHandle = scheduleHandle;
    }

    public File getFile() {
        return file;
    }

    public void addScheduleListener(final ScheduleListener listener) {
        listeners.add(listener);
    }

    public void removeScheduleListener(final ScheduleListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (ScheduleListener scheduleListener : listeners) {
            scheduleListener.cancelled(this);
        }
    }
}
