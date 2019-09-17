package ir.theartofliving.honarezendegi.Activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.github.meness.Library.Tag.TagGroup;
import io.github.meness.Library.Utils.Utility;
import ir.theartofliving.honarezendegi.AppSingleton;
import ir.theartofliving.honarezendegi.G;
import ir.theartofliving.honarezendegi.Model.Category;
import ir.theartofliving.honarezendegi.R;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener
{
    private TextView txtAuthor;
    private RadioButton rdbtnّFarzin;
    private TextView txtDate;
    private TextView txtTitle;
    private TextView txtCategories;
    private Button btnFrom;
    private Button btnTo;
    private TextView btnFilter;
    private TextView btnCancel;
    private TextView btnClear;
    private TextView txtSelectedCategory;
    private MaterialProgressBar progressBar;
    private TagGroup tagGroup;


    private static final String DATE_PICKER = "DatePickerDialog";
    private String dateFrom = null;
    private String dateTo = null;
    private boolean dateIsSelected = false;

    private int request = -1;
    private final int SELECT_FROM = 156;
    private final int SELECT_TO = 654;

    private int selectedCategory = -1;

    private ArrayList<Category> categories = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        findViews();
        setDefaultData();
    }

    private void findViews()
    {
        txtAuthor = (TextView) findViewById(R.id.txtAuthor);
        rdbtnّFarzin = (RadioButton) findViewById(R.id.rdbtnSoheil);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtCategories = (TextView) findViewById(R.id.txtCategories);
        txtSelectedCategory = (TextView) findViewById(R.id.txtSelectedCategory);
        btnFrom = (Button) findViewById(R.id.btnFrom);
        btnTo = (Button) findViewById(R.id.btnTo);
        btnFilter = (TextView) findViewById(R.id.btnFilter);
        btnCancel = (TextView) findViewById(R.id.btnCancel);
        btnClear = (TextView) findViewById(R.id.btnClear);
        progressBar = (MaterialProgressBar) findViewById(R.id.progressBar);
        tagGroup = (TagGroup) findViewById(R.id.tagGroup);
    }

    private void setDefaultData()
    {
        float width = G.widthPX * (9f / 10f);
        getWindow().setLayout((int) width, ViewGroup.LayoutParams.WRAP_CONTENT);

        rdbtnّFarzin.setTypeface(G.face);

        txtAuthor.setTypeface(G.face);
        txtDate.setTypeface(G.face);
        txtTitle.setTypeface(G.face);
        txtCategories.setTypeface(G.face);
        txtSelectedCategory.setTypeface(G.face);

        btnFrom.setTypeface(G.face);
        btnTo.setTypeface(G.face);
        btnCancel.setTypeface(G.face);
        btnFilter.setTypeface(G.face);
        btnClear.setTypeface(G.face);

        btnFrom.setText("از تاریخ");
        btnTo.setText("تا تاریخ");
        txtSelectedCategory.setText("");
        txtCategories.setText("دسته بندی ها");

        tagGroup.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        btnFrom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PersianCalendar now = new PersianCalendar();
                DatePickerDialog dialog = DatePickerDialog.newInstance(FilterActivity.this,
                        now.getPersianYear(),
                        now.getPersianMonth(),
                        now.getPersianDay());
                dialog.setThemeDark(false);
                dialog.show(getFragmentManager(), DATE_PICKER);
                request = SELECT_FROM;
            }
        });

        btnTo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PersianCalendar now = new PersianCalendar();
                DatePickerDialog dialog = DatePickerDialog.newInstance(FilterActivity.this,
                        now.getPersianYear(),
                        now.getPersianMonth(),
                        now.getPersianDay());
                dialog.setThemeDark(false);
                dialog.show(getFragmentManager(), DATE_PICKER);
                request = SELECT_TO;
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String filterData = "";

                filterData += generateAuthorFilter();
                if (!filterData.equals(""))
                    filterData += "&&";
                filterData += "&&" + generateDateFilter();
                if (!filterData.equals(""))
                    filterData += "&&";
                filterData += generateCategoryFilter();

                Intent intent = new Intent();
                intent.putExtra("filterData", filterData);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.putExtra("filterData", "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        getCategories();
    }

    private String generateAuthorFilter()
    {
        String filterText = "";
        if (rdbtnّFarzin.isChecked())
            filterText += "author=8";

        return filterText;
    }

    private String generateDateFilter()
    {
        String filterText = "";
        if (dateIsSelected == true)
            filterText += "after=" + dateFrom + "&&before=" + dateTo;
        return filterText;
    }

    private String generateCategoryFilter()
    {
        String filterText = "";
        if (selectedCategory != -1)
            filterText += "categories=" + selectedCategory;
        return filterText;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {
        String symbol = "-";
        String date = Utility.getPersianData(year, monthOfYear, dayOfMonth, symbol);

        monthOfYear++;

        if (request == SELECT_FROM)
        {
            dateFrom = Utility.getGlobalData(year, monthOfYear, dayOfMonth, symbol);
            btnFrom.setText("از : " + date);
        }
        else if (request == SELECT_TO)
        {
            dateTo = Utility.getGlobalData(year, monthOfYear, dayOfMonth, symbol);
            btnTo.setText("تا : " + date);
        }

        if (dateFrom != null && dateTo != null)
        {
            if (Utility.hasPermission(dateFrom, dateTo, symbol))
                dateIsSelected = true;
            else
            {
                dateIsSelected = false;
                Toast.makeText(getApplicationContext(), "بازه انتخابی اشتباه است", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getCategories()
    {
        String url = "http://honarzendegi.com/wp-json/wp/v2/categories";

        Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                Gson gson = new Gson();
                Category[] mCategories = gson.fromJson(response.toString(), Category[].class);
                List<String> tags = new ArrayList<>();
                for (int i = 1; i < mCategories.length; i++)
                {
                    categories.add(mCategories[i]);
                    tags.add(mCategories[i].getName());
                }

                tagGroup.setTags(G.face, tags);
                tagGroup.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener()
                {
                    @Override
                    public void onTagClick(String s)
                    {
                        txtCategories.setText("دسته بندی ها : ");
                        txtSelectedCategory.setText(s);
                        for (int i=0; i < categories.size() ; i++)
                        {
                            if (categories.get(i).getName().equals(s))
                            {
                                selectedCategory = categories.get(i).getId();
                                break;
                            }
                        }
                    }
                });

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), "خطا در دریافت", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        };

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}