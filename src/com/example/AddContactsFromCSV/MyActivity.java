package com.example.AddContactsFromCSV;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private Button button;

    private static int request_code_for_selection=1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        selectFile();
    }

    private void selectFile() {
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Entered after onclick");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                Intent i=Intent.createChooser(intent,"Select Contacts Csv File");

                try{

                    startActivityForResult(i,request_code_for_selection);
                }
                catch(ActivityNotFoundException e)
                {
                    System.out.println("Inside exception");
                }
            }
        });
    }




    private void addContact(String name, String phone) {
        String DisplayName = name;
        String MobileNumber = phone;

        ArrayList<ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers


        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        if(requestCode == request_code_for_selection)
            if (resultCode == RESULT_OK) {
                Uri returnUri = data.getData();
                System.out.println("Inside successcallback");
                System.out.println("ReturnUri:"+returnUri);
                File file = new File(returnUri.getPath());
                System.out.println("File Object:"+file);
                System.out.println(file.getAbsolutePath());
                System.out.println(file.getName());
                BufferedReader br=null;
                String line;
                String splitChar=",";
                try{
                    FileReader fr=new FileReader(file);
                    br= new BufferedReader(fr);
                    StringBuffer buffer = new StringBuffer();
                    int count=0;
                    while((line=br.readLine())!=null)
                    {
                        String[] record = line.split(splitChar);
                        System.out.println("Name:"+record[0]+" "+record[1]+"---"+"Phone:"+record[10]+"\n");
                        addContact(record[0]+" "+record[1],record[10]);
                        count++;
                     }
                    System.out.println("Inside Activity-1.1");
                    Toast.makeText(getApplicationContext(),"Total contacts added:"+count,Toast.LENGTH_SHORT).show();
                     }
                catch(FileNotFoundException ex)
                {
                    System.out.println("File is not found");
                }
                catch(IOException ex)
                {
                    System.out.println("IO Exception occured in Buffered Reader");
                }

            }
            else if(resultCode == RESULT_CANCELED)
            {
                System.out.println("Selection failed");
            }
    }


}
