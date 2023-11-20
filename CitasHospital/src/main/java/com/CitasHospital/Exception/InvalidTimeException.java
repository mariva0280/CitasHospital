package com.CitasHospital.Exception;

import ch.qos.logback.core.encoder.EchoEncoder;

public class InvalidTimeException extends Exception {
    public InvalidTimeException(String s) {
        super(s);
    }
}
