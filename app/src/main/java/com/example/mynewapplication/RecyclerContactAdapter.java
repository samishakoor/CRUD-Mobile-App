package com.example.mynewapplication;
import android.net.Uri;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.ConversationActions;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import android.widget.Filterable;
import android.text.Spannable;

public class RecyclerContactAdapter extends RecyclerView.Adapter<RecyclerContactAdapter.ViewHolder>
{

    Context context;
    ArrayList<ContactModel> contacts_arr;
    Uri updatedImageUri;
    CircleImageView dialog_img;
    String searchQuery;
    int flag;

    RecyclerContactAdapter(Context con) {
        this.context = con;
        contacts_arr=new ArrayList<>();
    }

    public void setContacts(ArrayList<ContactModel> filtered_contacts,int flag)
    {
      this.contacts_arr=filtered_contacts;
      this.flag=flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_content, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        int pos=holder.getAdapterPosition();

        holder.img.setImageURI(contacts_arr.get(pos).img);
        holder.upd.setImageResource(contacts_arr.get(pos).update_img);
        holder.rem.setImageResource(contacts_arr.get(pos).remove_img);
        holder.name.setText(contacts_arr.get(pos).Username);
        holder.num.setText(contacts_arr.get(pos).number);
        holder.time.setText(contacts_arr.get(pos).timeStamp);


        updateContact(holder);
        removeContact(holder);
        displayContact(holder);



    }

    @Override
    public int getItemCount() {
        return contacts_arr.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, num, time;
        CircleImageView img;
        ImageView upd,rem;
        LinearLayout clickable_recycler_item;

        public ViewHolder(@NonNull View viewItem) {
            super(viewItem);

            name = itemView.findViewById(R.id.textView1);
            num = itemView.findViewById(R.id.textView2);
            time = itemView.findViewById(R.id.timeStampId);
            img = itemView.findViewById(R.id.imgContact);
            upd=itemView.findViewById(R.id.UpdateContact);
            rem=itemView.findViewById(R.id.RemoveContact);
            clickable_recycler_item=itemView.findViewById(R.id.outer_linear_layout);
        }

    }


    public void handleActivityResultOfAdapter(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 2)
        {
            if(resultCode==Activity.RESULT_OK)
            {
                updatedImageUri=data.getData();
                dialog_img.setImageURI(updatedImageUri);
            }
            if(resultCode == Activity.RESULT_CANCELED)
            {
            }

        }

    }


    private void updateContact(@NonNull ViewHolder holder){
        holder.upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(context);
                dialog.setContentView(R.layout.add_update_contact);

                EditText edtName = dialog.findViewById(R.id.edtName);
                EditText edtNumber = dialog.findViewById(R.id.edtNumber);
                Button btnAction = dialog.findViewById(R.id.btnAction);
                dialog_img=dialog.findViewById(R.id.dialog_contact_img);


                TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                txtTitle.setText("Update Contact");
                btnAction.setText("Update");

                edtName.setText(contacts_arr.get(holder.getAdapterPosition()).Username);
                edtNumber.setText(contacts_arr.get(holder.getAdapterPosition()).number);

                updatedImageUri=contacts_arr.get(holder.getAdapterPosition()).img;
                dialog_img.setImageURI(updatedImageUri);

                dialog_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =new Intent(Intent.ACTION_PICK);
                        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        ((Activity) context).startActivityForResult(intent,2);
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
                            Toast.makeText(context, "Please Enter Name", Toast.LENGTH_SHORT).show();
                        }

                        if (!actualNumber .equals(""))
                        {
                            num = actualNumber;
                        } else
                        {
                            Toast.makeText(context,"Please Enter Contact Number", Toast.LENGTH_SHORT).show();
                        }

                        if( num.length()!=11 )
                        {
                            Toast.makeText(context, "The length of Contact Number should be 11 digits", Toast.LENGTH_SHORT).show();
                        }

                        MainActivity m=new MainActivity();
                        ContactModel c =new ContactModel(updatedImageUri,R.drawable.update,R.drawable.delete,num,m.capitalizeFirstWord(name));
                        c.SetTimeStamp(c.getCurrentTime());


                        ((MainActivity) context).updateContact(holder.getAdapterPosition(),c,flag);

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }
    private void removeContact(@NonNull ViewHolder holder)
    {

        holder.rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Remove Contact")
                        .setMessage("Are you sure to remove this contact?")
                        .setIcon(R.drawable.dialog_delete)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                int position=holder.getAdapterPosition();
                                ContactModel contact =contacts_arr.get(position);

                                ((MainActivity) context).deleteContact(contact,flag);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                builder.show();
            }
        });

    }
    private void displayContact(@NonNull ViewHolder holder){
        holder.clickable_recycler_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ContactModel cm=contacts_arr.get(holder.getAdapterPosition());
                Intent intent=new Intent(context,DisplayContactActivity.class);
                intent.setData(cm.img);
                intent.putExtra("name",cm.Username);
                intent.putExtra("number",cm.number);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ((Activity )context).startActivityForResult(intent, 1);
            }
        });}








}



