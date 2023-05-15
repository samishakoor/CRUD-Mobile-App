package com.example.mynewapplication;
import android.net.Uri;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mynewapplication.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import androidx.appcompat.widget.SearchView;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    RecyclerContactAdapter adapter;
    ArrayList<ContactModel> contacts_arr = new ArrayList<>();
    ArrayList<ContactModel> filteredResults;
    FloatingActionButton btnOpenDialog;
    SearchView sView;
    Uri selectedImageUri;
    CircleImageView dialog_img;

    int flag=-1;

    private final int GALLERY_REQ_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recyclerview);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecyclerContactAdapter(MainActivity.this);
        recycler.setAdapter(adapter);

        implementSearchBar(adapter);
        addContact(adapter,recycler);
    }

    public void deleteContact(ContactModel contact,int flag)
    {
        if (flag==0) {
            if (!contacts_arr.isEmpty()) {
                contacts_arr.remove(contact);
            }
        }

        if (flag==1)
        {
            if (!contacts_arr.isEmpty()) {
                contacts_arr.remove(contact);
            }
            if (!filteredResults.isEmpty()) {
                filteredResults.remove(contact);
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void updateContact(int position,ContactModel contact,int flag)
    {

        int originalPosition=-1;

        if (flag==0)
        {
            if (this.contacts_arr != null && this.contacts_arr.size() > 0) {
                originalPosition = this.contacts_arr.indexOf(adapter.contacts_arr.get(position));
            }


            if (originalPosition >= 0) {
                this.contacts_arr.set(originalPosition, contact);
            }
        }

        if (flag==1){

            if (this.contacts_arr != null && this.contacts_arr.size() > 0) {
                originalPosition = this.contacts_arr.indexOf(adapter.contacts_arr.get(position));
            }


            if (originalPosition >= 0) {
                this.contacts_arr.set(originalPosition, contact);
            }

            if (!filteredResults.isEmpty()) {
                filteredResults.set(position, contact);
            }
        }



        adapter.notifyItemChanged(position);
    }



    public String capitalizeFirstWord(String name) {
        // stores each characters to a char array
        char[] charArray = name.toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {

            // if the array element is a letter
            if(Character.isLetter(charArray[i])) {

                // check space is present before the letter
                if(foundSpace) {

                    // change the letter into uppercase
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            }

            else {
                // if the new character is not character
                foundSpace = true;
            }
        }

        // convert the char array to the string
        return String.valueOf(charArray);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 )
        {
            if(resultCode==RESULT_OK)
            {
            }
            if(resultCode == RESULT_CANCELED)
            {
            }
        }

        if(requestCode == 2 )
        {
         adapter.handleActivityResultOfAdapter(requestCode, resultCode, data);
        }

        if(requestCode == GALLERY_REQ_CODE)
        {
            if(resultCode==RESULT_OK && data.getData()!=null)
            {
                selectedImageUri=data.getData();
                dialog_img.setImageURI(selectedImageUri);
            }
            if(resultCode == RESULT_CANCELED)
            {

            }
        }
    }

    private void addContact(RecyclerContactAdapter adapter ,RecyclerView recycler)
    {
        btnOpenDialog=findViewById(R.id.btnOpenDialog);

        btnOpenDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_update_contact);

                EditText edtName = dialog.findViewById(R.id.edtName);
                EditText edtNumber = dialog.findViewById(R.id.edtNumber);
                Button btnAction = dialog.findViewById(R.id.btnAction);

                dialog_img=dialog.findViewById(R.id.dialog_contact_img);
                dialog_img.setImageResource(R.drawable.user);

                selectedImageUri=Uri.parse("android.resource://"+getPackageName()+"/"+R.drawable.user);

                dialog_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent iGallery =new Intent(Intent.ACTION_PICK);
                        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(iGallery,GALLERY_REQ_CODE);
                    }
                });

                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String actualName = edtName.getText().toString();
                        String actualNumber = edtNumber.getText().toString();

                        String name = "";
                        String num="";

                        if (!actualName.equals("")) {
                            name = actualName;
                        } else
                        {
                            Toast.makeText(MainActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                        }

                        if (!actualNumber .equals("")) {
                            num = actualNumber;
                        } else
                        {
                            Toast.makeText(MainActivity.this, "Please Enter Contact Number", Toast.LENGTH_SHORT).show();
                        }

                        if( num.length()!=11 )
                        {
                            Toast.makeText(MainActivity.this, "The length of Contact Number should be 11 digits", Toast.LENGTH_SHORT).show();
                        }

                        if (!name.equals("") && !num.equals("") && num.length()==11)
                        {
                            ContactModel c = new ContactModel(selectedImageUri,R.drawable.update,R.drawable.delete,num,capitalizeFirstWord(name));
                            c.SetTimeStamp(c.getCurrentTime());
                            contacts_arr.add(c);
                            flag=0;
                            adapter.setContacts(contacts_arr,flag);
                            adapter.notifyItemInserted(contacts_arr.size()-1);
                            recycler.scrollToPosition(contacts_arr.size() - 1);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void implementSearchBar(RecyclerContactAdapter adapter)
    {

        sView=findViewById(R.id.search_view);
        sView.clearFocus();

        sView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                sView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {

                filteredResults=new ArrayList<>();

                for (ContactModel obj:contacts_arr)
                {
                    if(obj.Username.toLowerCase().contains(text.toLowerCase()))
                    {
                        filteredResults.add(obj);
                    }
                }

                if(filteredResults.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "No Contact(s) Found", Toast.LENGTH_SHORT).show();
                }

                flag=1;
                adapter.setContacts(filteredResults,flag);
                adapter.notifyDataSetChanged();
                return true;
            }
        });


        sView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                sView.clearFocus();
                return false;
            }
        });


    }


}

















