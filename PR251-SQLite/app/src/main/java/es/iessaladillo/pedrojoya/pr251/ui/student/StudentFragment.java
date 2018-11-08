package es.iessaladillo.pedrojoya.pr251.ui.student;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import es.iessaladillo.pedrojoya.pr251.Constants;
import es.iessaladillo.pedrojoya.pr251.R;
import es.iessaladillo.pedrojoya.pr251.data.RepositoryImpl;
import es.iessaladillo.pedrojoya.pr251.data.local.DbHelper;
import es.iessaladillo.pedrojoya.pr251.data.local.StudentDao;
import es.iessaladillo.pedrojoya.pr251.data.local.model.Student;
import es.iessaladillo.pedrojoya.pr251.ui.main.MainActivityViewModel;
import es.iessaladillo.pedrojoya.pr251.utils.ClickToSelectEditText;
import es.iessaladillo.pedrojoya.pr251.utils.KeyboardUtils;

@SuppressWarnings({"unchecked", "unused"})
public class StudentFragment extends Fragment {

    private EditText txtName;
    private ClickToSelectEditText<String> spnGrade;
    private EditText txtPhone;
    private EditText txtAddress;
    private TextInputLayout tilName;
    private TextInputLayout tilGrade;
    private TextInputLayout tilPhone;

    private boolean editMode;
    private long studentId;
    private StudentFragmentViewModel viewModel;
    private MainActivityViewModel activityViewModel;

    public static StudentFragment newInstance() {
        return new StudentFragment();
    }

    public static StudentFragment newInstance(long studentId) {
        StudentFragment studentFragment = new StudentFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(Constants.EXTRA_STUDENT_ID, studentId);
        studentFragment.setArguments(arguments);
        return studentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        obtainArguments(getArguments());
        super.onCreate(savedInstanceState);
    }

    private void obtainArguments(Bundle arguments) {
        if (arguments != null && arguments.containsKey(Constants.EXTRA_STUDENT_ID)) {
            editMode = true;
            studentId = arguments.getLong(Constants.EXTRA_STUDENT_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityViewModel = ViewModelProviders.of(requireActivity()).get(
                MainActivityViewModel.class);
        viewModel = ViewModelProviders.of(this, new StudentFragmentViewModelFactory(
                new RepositoryImpl(StudentDao.getInstance(
                        DbHelper.getInstance(requireContext().getApplicationContext()))),
                activityViewModel)).get(StudentFragmentViewModel.class);
        initViews(getView());
        if (editMode) {
            viewModel.getStudent(studentId).observe(getViewLifecycleOwner(), this::showStudent);
        }
    }

    private void initViews(View view) {
        tilName = ViewCompat.requireViewById(view, R.id.tilName);
        tilGrade = ViewCompat.requireViewById(view, R.id.tilGrade);
        tilPhone = ViewCompat.requireViewById(view, R.id.tilPhone);
        TextInputLayout tilAddress = ViewCompat.requireViewById(view, R.id.tilAddress);
        txtName = ViewCompat.requireViewById(view, R.id.txtName);
        spnGrade = ViewCompat.requireViewById(view, R.id.txtGrade);
        txtPhone = ViewCompat.requireViewById(view, R.id.txtPhone);
        txtAddress = ViewCompat.requireViewById(view, R.id.txtAddress);

        setupToolbar();
        setupFab();
        txtAddress.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveStudent();
                return true;
            }
            return false;
        });
        loadGrades();
    }

    private void setupFab() {
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_save_white_24dp);
        fab.setOnClickListener(v -> saveStudent());
    }

    private void setupToolbar() {
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setTitle(
                    editMode ? R.string.student_fragment_edit_student : R.string.student_fragment_add_student);
        }
    }

    private void loadGrades() {
        ArrayAdapter<CharSequence> gradesAdapter = ArrayAdapter.createFromResource(
                requireActivity(), R.array.grades, android.R.layout.simple_list_item_1);
        spnGrade.setAdapter(gradesAdapter);
        spnGrade.setOnItemSelectedListener((item, selectedIndex) -> spnGrade.setText(item));
    }

    private void showErrorLoadingStudentAndFinish() {
        Toast.makeText(requireActivity(), R.string.student_fragment_error_loading_student,
                Toast.LENGTH_LONG).show();
        requireActivity().finish();
    }

    private void saveStudent() {
        if (isValidForm()) {
            Student student = createStudent();
            if (editMode) {
                student.setId(studentId);
                viewModel.updateStudent(student);
            } else {
                viewModel.addStudent(student);
            }
            KeyboardUtils.hideSoftKeyboard(requireActivity());
            requireActivity().onBackPressed();
        }
    }

    private boolean isValidForm() {
        boolean valido = true;
        if (!checkRequiredEditText(txtName, tilName)) {
            valido = false;
        }
        if (!checkRequiredEditText(spnGrade, tilGrade)) {
            valido = false;
        }
        if (!checkRequiredEditText(txtPhone, tilPhone)) {
            valido = false;
        }
        return valido;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkRequiredEditText(EditText txt, TextInputLayout til) {
        if (TextUtils.isEmpty(txt.getText().toString())) {
            til.setErrorEnabled(true);
            til.setError(getString(R.string.student_fragment_required_field));
            return false;
        } else {
            til.setErrorEnabled(false);
            til.setError("");
            return true;
        }
    }

    private void showStudent(Student student) {
        if (student == null) {
            activityViewModel.setInfoMessage(R.string.student_fragment_student_not_found);
            requireActivity().onBackPressed();
        } else {
            txtName.setText(student.getName());
            spnGrade.setText(student.getGrade());
            txtPhone.setText(student.getPhone());
            txtAddress.setText(student.getAddress());
        }
    }

    private Student createStudent() {
        Student student = new Student();
        student.setName(txtName.getText().toString());
        student.setPhone(txtPhone.getText().toString());
        student.setAddress(txtAddress.getText().toString());
        //noinspection ConstantConditions
        student.setGrade(spnGrade.getText().toString());
        return student;
    }

}