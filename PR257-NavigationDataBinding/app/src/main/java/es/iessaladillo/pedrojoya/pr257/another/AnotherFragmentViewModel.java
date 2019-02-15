package es.iessaladillo.pedrojoya.pr257.another;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

@SuppressWarnings("WeakerAccess")
public class AnotherFragmentViewModel extends ViewModel {

    private final MutableLiveData<String> _name = new MutableLiveData<>();

    public LiveData<String> getName() {
        return _name;
    }

    AnotherFragmentViewModel(@NonNull Bundle arguments) {
        AnotherFragmentArgs anotherFragmentArgs = AnotherFragmentArgs.fromBundle(arguments);
        _name.setValue(anotherFragmentArgs.getName());
    }

}