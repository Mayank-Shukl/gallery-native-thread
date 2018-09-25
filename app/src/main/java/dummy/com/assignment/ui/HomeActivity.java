package dummy.com.assignment.ui;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import dummy.com.assignment.BR;
import dummy.com.assignment.R;
import dummy.com.assignment.mypicasso.PhotoManager;
import dummy.com.assignment.viewmodel.HomeViewModel;

/**
 * Main Activity
 */
public class HomeActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        HomeViewModel viewModel = new HomeViewModel(this);
        viewDataBinding.setVariable(BR.viewModel, viewModel);
    }

    @Override
    protected void onDestroy() {
        PhotoManager.cancelAll();
        super.onDestroy();
    }
}

