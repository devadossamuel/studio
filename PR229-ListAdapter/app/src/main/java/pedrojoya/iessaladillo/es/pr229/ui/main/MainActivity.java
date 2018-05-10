package pedrojoya.iessaladillo.es.pr229.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import pedrojoya.iessaladillo.es.pr229.R;
import pedrojoya.iessaladillo.es.pr229.data.model.Student;
import pedrojoya.iessaladillo.es.pr229.recycleradapter.OnItemClickListener;
import pedrojoya.iessaladillo.es.pr229.recycleradapter.OnItemLongClickListener;


public class MainActivity extends AppCompatActivity implements OnItemClickListener<Student>, OnItemLongClickListener<Student> {

    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView lstStudents;
    private MainActivityAdapter adapter;
    private View emptyView;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        initViews();
    }

    private void initViews() {
        emptyView = ActivityCompat.requireViewById(this, R.id.emptyView);
        setupToolbar();
        setupRecyclerView();
        setupFab();
    }

    private void setupToolbar() {
        Toolbar toolbar = ActivityCompat.requireViewById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupFab() {
        FloatingActionButton fab = ActivityCompat.requireViewById(this, R.id.fab);
        fab.setOnClickListener(view -> addStudent());
    }

    private void setupRecyclerView() {
        adapter = new MainActivityAdapter();
        adapter.setEmptyView(emptyView);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        lstStudents = ActivityCompat.requireViewById(this, R.id.lstStudents);
        lstStudents.setHasFixedSize(true);
        lstStudents.setAdapter(adapter);
        lstStudents.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        lstStudents.setItemAnimator(new DefaultItemAnimator());
        adapter.submitList(viewModel.getStudents(false));
    }

    private void addStudent() {
        viewModel.insertStudent();
        adapter.submitList(viewModel.getStudents(true));
    }

    private void updateStudent(Student student) {
        Student newStudent = (new Student(student)).reverseName();
        viewModel.updateStudent(student, newStudent);
        adapter.submitList(viewModel.getStudents(true));
    }

    private void deleteStudent(Student student) {
        viewModel.removeStudent(student);
        adapter.submitList(viewModel.getStudents(true));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuSort) {
            adapter.submitList(viewModel.toggleOrder());
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(View view, Student item, int position, long id) {
        updateStudent(item);
    }

    @Override
    public boolean onItemLongClick(View view, Student item, int position, long id) {
        deleteStudent(item);
        return true;
    }

}