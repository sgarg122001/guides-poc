package com.adobe.aem.guides.wknd.core.models;

import java.util.List;

/**
 * Represents the ThreePayAndShortCode AEM Component.
 **/
public interface ThreePayAndShortCodesModel {
    public String getShortCodesTitle();
    public String getFieldText();
    public String getLabel();
    public String getErrorMsg();
    public String getButtonTitle();

    public boolean isEmpty();

}