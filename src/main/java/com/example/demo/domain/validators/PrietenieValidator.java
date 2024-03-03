package com.example.demo.domain.validators;

import com.example.demo.domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie>{
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        String err="";
        if (entity.getId().getLeft()<0)
            err+="Invalid ID1";
        if (entity.getId().getRight()<0)
            err+="Invalid ID2";
        if( err!="")
            throw new ValidationException(err);
        if(entity.getId().getLeft().equals(entity.getId().getRight()))
            throw new ValidationException("Can't be friend with yourself");
    }

}
