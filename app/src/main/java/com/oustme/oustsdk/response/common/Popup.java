package com.oustme.oustsdk.response.common;

import androidx.annotation.Keep;

import java.util.List;
import java.util.Map;

/**
 * Created by shilpysamaddar on 07/03/17.
 */

@Keep
public class Popup {
    private String content;
    private String header;
    private int nButtons;
    private List<OustPopupButton> buttons;
    private boolean modal;
    private OustPopupCategory category;
    private OustPopupType type;
    private String bgImage;
    private String icon;
    private Map<String,String> oustPopupData;
    private boolean isErrorPopup;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getnButtons() {
        return nButtons;
    }

    public void setnButtons(int nButtons) {
        this.nButtons = nButtons;
    }

    public List<OustPopupButton> getButtons() {
        return buttons;
    }

    public void setButtons(List<OustPopupButton> buttons) {
        this.buttons = buttons;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public OustPopupCategory getCategory() {
        return category;
    }

    public void setCategory(OustPopupCategory category) {
        this.category = category;
    }

    public OustPopupType getType() {
        return type;
    }

    public void setType(OustPopupType type) {
        this.type = type;
    }

    public String getBgImage() {
        return bgImage;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Map<String, String> getOustPopupData() {
        return oustPopupData;
    }

    public void setOustPopupData(Map<String, String> oustPopupData) {
        this.oustPopupData = oustPopupData;
    }

    public boolean isErrorPopup() {
        return isErrorPopup;
    }

    public void setErrorPopup(boolean errorPopup) {
        isErrorPopup = errorPopup;
    }
}
