package com.blockcv.events;

import java.io.File;

public class ImportEvent {
    File xml;

    public ImportEvent(File xml) {
        this.xml=xml;
    }

    public File getXml(){
        return xml;
    }
}
