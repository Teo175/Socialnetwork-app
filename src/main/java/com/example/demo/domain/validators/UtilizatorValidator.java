package com.example.demo.domain.validators;

import com.example.demo.domain.Utilizator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilizatorValidator implements Validator<Utilizator> {

    @Override
    public void validate(Utilizator entity) throws ValidationException {
        ValidUserName(entity);
    }

    private void ValidUserName(Utilizator entity) {
        String regex = "^[A-Z][a-z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(entity.getFirstName());
        if (!matcher.find())
            throw new ValidationException("First Name Invalid");
        matcher = pattern.matcher(entity.getLastName());
        if (!matcher.find())
            throw new ValidationException("Last Name Invalid");
        if(entity.getId()<0)
            throw new ValidationException("ID must be positive");
    }

}

