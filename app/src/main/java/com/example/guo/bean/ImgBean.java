package com.example.guo.bean;

import java.io.Serializable;

public class ImgBean implements Serializable {
    private static final long serialVersionUID = -5976154831496716111L;
    private String src;
    private String source;
    private String detail;
    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

}
