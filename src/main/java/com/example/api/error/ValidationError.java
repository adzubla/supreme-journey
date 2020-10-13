package com.example.api.error;

import java.util.ArrayList;
import java.util.List;

@lombok.Data
@lombok.NoArgsConstructor
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true)
public class ValidationError extends ApiError {

    private List<FieldInfo> fieldInfo = new ArrayList<>();

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.ToString
    public static class FieldInfo {

        private String objectName;
        private String field;
        private String code;
        private String defaultMessage;
        private Object rejectedValue;
    }

}