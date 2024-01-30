package hua.dit.oop2.assig.GUI;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import hua.dit.oop2.assig.core.Event;
import hua.dit.oop2.assig.handlers.ICalHandler;


public class CalendarFileChooser extends JFileChooser {

    private ICalHandler icalHandler;

    public CalendarFileChooser() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("iCal Files (*.ics)", "ics");
        this.setFileFilter(filter);
        this.setCurrentDirectory(new File(System.getProperty("user.home")));
        this.setFileSelectionMode(JFileChooser.FILES_ONLY);

        icalHandler = new ICalHandler();
    }


    // Method to read contents of the selected iCal file
    public List<Event> openICalFile(File selectedFile) throws IOException {
        int returnValue = this.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = this.getSelectedFile();
            return icalHandler.readFromICalFile(file.getAbsolutePath());
        }
        return null;
    }


    // Method to write data to an iCal file
    public void saveICalFile(List<Event> events, File selectedFile) throws IOException {
        int returnValue = this.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = this.getSelectedFile();
            icalHandler.writeToICalFile(file.getAbsolutePath(), events);
        }
    }
}

