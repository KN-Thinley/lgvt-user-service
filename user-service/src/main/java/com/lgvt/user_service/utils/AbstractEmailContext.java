package com.lgvt.user_service.utils;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

public abstract class AbstractEmailContext {
    protected final static String DEFAULT_ENCODING = "UTF-8";
    protected final static String DEFAULT_CONTENT_TYPE = "text/html";

    protected String from;
    protected String to;
    protected String subject;
    protected String templateLocation;
    protected String encoding;
    protected String contentType;
    protected int otp;
    private Map<String, Object> context;

    public AbstractEmailContext() {
        this.encoding = DEFAULT_ENCODING;
        this.contentType = DEFAULT_CONTENT_TYPE;
        this.context = new HashMap<>();
    }

    public <T> void init(T context) {

    }

    public Object put(String key, Object value) {
        return key == null ? null : this.context.put(key.intern(), value.toString());
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setToken(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setToken'");
    }

    public void buildVerificationUrl(String baseUrl, String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buildVerificationUrl'");
    }
}
