package courseProject.fullSV.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PhoneValidator implements ConstraintValidator<PhoneConstraint, String> {

    @Override
    public void initialize(PhoneConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(s)) return true;
        String first = s.substring(0, 2);
        if(!first.equals("03") && !first.equals("09")) return false;
        return s.startsWith(first) && s.matches("[0-9]+")
                && s.length() > 9 && s.length() < 14;
    }
}
