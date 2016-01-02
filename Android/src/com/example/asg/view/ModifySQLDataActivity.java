package com.example.asg.view;

import com.example.asg.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.Editable;

public class ModifySQLDataActivity extends Activity implements OnClickListener {

	private TextView textview_tpye;
	private TextView textview_id;
	private EditText edit_name;
	private EditText edit_aplow;
	private EditText edit_apup;
	private EditText edit_rplow;
	private EditText edit_rpup;
	private Button but_back;
	private Button but_addormodify;
	private String type;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_sqldata);
       // getActionBar().setDisplayHomeAsUpEnabled(true);
        this.but_addormodify=(Button) this.findViewById(R.id.button_modifyoradd);
        this.but_back=(Button) this.findViewById(R.id.button_backback);
        this.textview_id=(TextView) this.findViewById(R.id.textView_id);
        this.textview_tpye=(TextView) this.findViewById(R.id.textView_opetype);
        this.edit_name=(EditText) this.findViewById(R.id.editText_appname);
        this.edit_aplow=(EditText) this.findViewById(R.id.editText_aplow);
        this.edit_apup=(EditText) this.findViewById(R.id.editText_apup);
        this.edit_rplow=(EditText) this.findViewById(R.id.editText_rplow);
        this.edit_rpup=(EditText) this.findViewById(R.id.editText_rpup);
        
        this.textview_id.setText("");
        Bundle bundle=this.getIntent().getExtras();
        Log.i("if null",String.valueOf(bundle==null));
//        Log.i("type",bundle.getString("operateType"));
       // Log.i("id",bundle.getString("id"));
       // Log.i("name",bundle.getString("name"));
        //Log.i("a",bundle.getString("apup"));
        //Log.i("r",bundle.getString("rplow"));
        
         type=bundle.getString("operateType");
         Log.i("type", type);
        if(type.equals("add"))
        {
        	Log.i("add","add success" );
        	this.textview_tpye.setText("�����µļ�¼��Ϣ��");
        	this.but_addormodify.setText("���Ӽ�¼");
        }
        else if(type.equals("modify"))
        {
        	Log.i("success modify", "�޸ĳɹ�");
        	this.textview_tpye.setText("�޸�һ����¼��Ϣ��");
        	this.but_addormodify.setText("�޸ļ�¼");
        
            this.textview_id.setText(bundle.getString("id"));
            //Log.i("??",bundle.getString("id"));
            this.edit_name.setText(bundle.getString("name"));
            this.edit_apup.setText(bundle.getString("apup"));
            this.edit_aplow.setText(bundle.getString("aplow"));
            this.edit_rplow.setText(bundle.getString("rplow"));
            this.edit_rpup.setText(bundle.getString("rpup"));
        }
        

       
       
       this.but_back.setOnClickListener(this);
       this.but_addormodify.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_modify_sqldata, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==this.but_addormodify)
		{
			
			String id=(String) this.textview_id.getText();
			String  name= ((Editable)(this.edit_name.getText())).toString();
			String apup= ((Editable)(this.edit_apup.getText())).toString();
			String aplow= ((Editable)(this.edit_aplow.getText())).toString();
			String rplow= ((Editable)(this.edit_rplow.getText())).toString();
			String rpup =((Editable)(this.edit_rpup.getText())).toString();
			
			Bundle bundle=new Bundle();
			if(name!=""&&apup!=""&&aplow!=""&&rplow!=""&&rpup!="")
				//֮����Ըĳ�һ���жϸ�ʽ�ķ���
			{
				bundle.putString("type",type);
				bundle.putString("status", "refresh");		//֪ͨҪ�������ݿ�ĸ���
				if(type.equals("modify"))
				{
					bundle.putInt("id",Integer.valueOf(id));
					Log.i("id~",id);
				}
				bundle.putString("name", name);
				bundle.putDouble("apup", Double.parseDouble(apup));
				bundle.putDouble("aplow", Double.parseDouble(aplow));
				bundle.putDouble("rpup", Double.parseDouble(rpup));
				bundle.putDouble("rplow", Double.parseDouble(rplow));
				
				Intent intent=new Intent();
				intent.setClass(this, ShowSQLActivity.class);
				intent.putExtras(bundle);
				this.startActivity(intent);
				this.finish();								
			}
			else
			{
				//��ʱʲô������                         ������ʾ�������                      ��ʱû���������ݵķ���
				new AlertDialog.Builder(this)   
				   .setTitle("error")  
				   .setMessage("���ݽ������ݸ�ʽ�������⣡��������")  
				   .setPositiveButton("����", null)   
				   .show();  			
			}
		}
		else if(v==this.but_back)
		{
			Intent intent=new Intent();
			intent.setClass(this,ShowSQLActivity.class );
			this.startActivity(intent);
			this.finish();
		}
	}

}