package com.si.mindhealth.converters;
import com.si.mindhealth.entities.enums.MoodLevel;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MoodLevelConverter implements AttributeConverter<MoodLevel, Integer> {
    @Override
    public Integer convertToDatabaseColumn(MoodLevel level) {
        return (level == null) ? null : level.getValue();
    }

    @Override
    public MoodLevel convertToEntityAttribute(Integer dbData) {
        return (dbData == null) ? null : MoodLevel.fromValue(dbData);
    }
}
