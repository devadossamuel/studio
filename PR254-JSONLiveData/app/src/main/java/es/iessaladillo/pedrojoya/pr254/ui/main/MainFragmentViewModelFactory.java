package es.iessaladillo.pedrojoya.pr254.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import es.iessaladillo.pedrojoya.pr254.data.Repository;

public class MainFragmentViewModelFactory implements ViewModelProvider.Factory {

    private final Repository repository;

    public MainFragmentViewModelFactory(Repository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainFragmentViewModel.class)) {
            return (T) new MainFragmentViewModel(repository);
        } else {
            throw new IllegalArgumentException(("Wrong modelViewClass"));
        }
    }

}