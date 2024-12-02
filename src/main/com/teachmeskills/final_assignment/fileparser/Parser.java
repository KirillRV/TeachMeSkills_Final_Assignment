package main.com.teachmeskills.final_assignment.fileparser;

import main.com.teachmeskills.final_assignment.constant.Constants;

import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Parser<T> {

    List<T> parseFiles(List<Path> files);

    static double findAmountInString(String amount){
        Pattern pattern = Pattern.compile(Constants.REGEX_EXP_SUM);
        Matcher matcher = pattern.matcher(amount.replace(",", "."));

        if (matcher.find()) {
            String value = matcher.group();
            return Double.parseDouble(value);
        } else {
            return 0;
        }
    }
}

