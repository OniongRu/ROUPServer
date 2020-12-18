package dataSend;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class DataObservableExposeStrategy implements ExclusionStrategy {

    private FieldAttributes fieldAttributes;

    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        /*Easy debug:
        if (fieldAttributes.getName().equals("password")) {
            System.out.println("Goose");
        }
        var attribute = fieldAttributes.getAnnotation(Observable.class);
        boolean isNotObservable = (fieldAttributes.getAnnotation(Observable.class) == null);
        boolean isNotObservable2 = (attribute == null);*/
        return fieldAttributes.getAnnotation(Observable.class) == null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
