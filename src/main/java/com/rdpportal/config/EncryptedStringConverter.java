package com.rdpportal.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter
@Component
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private final EncryptionConfig encryptionConfig;

    public EncryptedStringConverter(EncryptionConfig encryptionConfig) {
        this.encryptionConfig = encryptionConfig;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptionConfig.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptionConfig.decrypt(dbData);
    }
}
