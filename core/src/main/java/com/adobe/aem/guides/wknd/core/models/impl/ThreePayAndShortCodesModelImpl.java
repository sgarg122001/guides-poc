package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.models.ThreePayAndShortCodesModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {ThreePayAndShortCodesModel.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ThreePayAndShortCodesModelImpl implements ThreePayAndShortCodesModel {

    @ValueMapValue
    String shortCodesTitle;

    @ValueMapValue
    String fieldText;

    @ValueMapValue
    String label;

    @ValueMapValue
    String errorMsg;

    @ValueMapValue
    String buttonTitle;

    public String getShortCodesTitle() {
        return shortCodesTitle;
    }

    public String getFieldText() {
        return fieldText;
    }


    public String getLabel() {
        return label;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getButtonTitle() {
        return buttonTitle;
    }

    public boolean isEmpty() {
        final String shortCodesTitle = getShortCodesTitle();
        final String fieldText = getFieldText();
        final String label = getLabel();
        final String buttonTitle = getButtonTitle();
        final String errorMsg = getErrorMsg();
        if(StringUtils.isEmpty(shortCodesTitle) && StringUtils.isEmpty(fieldText) && StringUtils.isEmpty(label) && StringUtils.isEmpty(buttonTitle) && StringUtils.isEmpty(errorMsg)){
            return true;
        }
        return false;
    }
}
