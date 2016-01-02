package com.example.asg.services.meteraccess;

import java.io.*;


import android.util.Log;

import com.example.asg.util.CommonFunction;

public class GE  extends MeterFather{
	/**
	 * Ҫ��operationFile.txt����Ϊȫ���Ķ�GE���ݵı���
	 */
	//Environment.getExternalStorageDirectory()
	File readOperationFile = new File("sdcard/ReadOperationFile.txt");
	File writeOperationFile = new File("sdcard/WriteOperationFile.txt");
	File dataFile = new File("sdcard/dataFile.txt");
	/*File readOperationFile = new File("ReadOperationFile.txt");
	File writeOperationFile = new File("WriteOperationFile.txt");
	File dataFile = new File("dataFile.txt");*/
	
	public String[] readGEData(){
		String[] result = new String[5];
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String line = "";//operation line
		String response = "";
		int transitionID = 0;
		try{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(readOperationFile)));
			writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile)));
			
			while(true){
				line = reader.readLine();
				if(line == null){
					break;
				}
				response = ((GEConnector)connector).sendReadPackageAndShowResponse(line, transitionID++);
				if(response.equals("") || response.length() < 120){
					throw new Exception("����");
				}
				writer.write(response);
			}
			reader.close();
			writer.close();
			
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
			line = reader.readLine();
		
			//connection type
			result[4] = line.substring(3474, 3476);
			Log.i("value type", result[4]);
			//secVoltage
			result[3] = line.substring(3452, 3456);
			Log.i("value secVoltage", result[3]);
			//priVoltage
			result[2] = line.substring(3444, 3448);
			Log.i("value priVoltage", result[2]);
			//secCurrent
			result[1] = line.substring(3420, 3424);
			Log.i("value secCurrent", result[1]);
			//priCurrent
			result[0] = line.substring(3412, 3416);
			Log.i("value priCurrent", result[0]);
			
			reader.close();
			return result;
		}catch(Exception ex){
			Log.i("GE.readGEData", ex.getMessage());
			return null;
		}
	}
	
	/**
	 * �ı以�бȺ����ӷ�ʽ
	 * @param priCurrent
	 * @param secCurrent
	 * @param priVoltage
	 * @param secVoltage
	 * @param type
	 * @return
	 */
	public boolean changeGEDataFile(int priCurrent, int secCurrent, int priVoltage, int secVoltage, int type){
		BufferedReader reader = null;
		BufferedWriter writer = null;
		String line = "";
		char[] chars = null;
		String tempStr = "";
		try{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
			line = reader.readLine();
			
			//�����ʧ�ܣ��ͷ���
			if(line.equals("")){
				reader.close();
				return false;
			}
			chars = line.toCharArray();
			
			
			//secVoltage
			tempStr = CommonFunction.changeIntegerToHexStr(2, secVoltage, "");
			chars[3452] = tempStr.charAt(0);
			chars[3453] = tempStr.charAt(1);
			chars[3454] = tempStr.charAt(2);
			chars[3455] = tempStr.charAt(3);
			
			//priVoltage
			tempStr = CommonFunction.changeIntegerToHexStr(2, priVoltage, "");
			chars[3444] = tempStr.charAt(0);
			chars[3445] = tempStr.charAt(1);
			chars[3446] = tempStr.charAt(2);
			chars[3447] = tempStr.charAt(3);

			//secCurrent
			tempStr = CommonFunction.changeIntegerToHexStr(2, secCurrent, "");
			chars[3420] = tempStr.charAt(0);
			chars[3421] = tempStr.charAt(1);
			chars[3422] = tempStr.charAt(2);
			chars[3423] = tempStr.charAt(3);
			
			//priCurrent
			tempStr = CommonFunction.changeIntegerToHexStr(2, priCurrent, "");
			chars[3412] = tempStr.charAt(0);
			chars[3413] = tempStr.charAt(1);
			chars[3414] = tempStr.charAt(2);
			chars[3415] = tempStr.charAt(3);
			
			//connection type
			chars[3474] = '0';
			chars[3475] = String.valueOf(type).charAt(0);
			
			writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile)));
			line = String.valueOf(chars);
			
			String[] result = new String[5];
			//connection type
			result[4] = line.substring(3474, 3476);
			Log.i("GE.changeGEDataFile value type", result[4]+"   "+type);
			//secVoltage
			result[3] = line.substring(3452, 3456);
			Log.i("GE.changeGEDataFile value secVoltage", result[3] + "  "+secVoltage);
			//priVoltage
			result[2] = line.substring(3444, 3448);
			Log.i("GE.changeGEDataFile value priVoltage", result[2]+ "   "+priVoltage);
			//secCurrent
			result[1] = line.substring(3420, 3424);
			Log.i("GE.changeGEDataFile value secCurrent", result[1]+ "   "+secCurrent);
			//priCurrent
			result[0] = line.substring(3412, 3416);
			Log.i("GE.changeGEDataFile value priCurrent", result[0]+ "   "+priCurrent);
			
			
			writer.write(line);
			
			reader.close();
			writer.close();
			
			Log.i("GE.changeGEDataFile", "before return ");
			//write data back to meter
			return this.startCracking(writeOperationFile, dataFile, passwd);
		}catch(Exception ex){
			Log.i("GE.changeGEDataFile", "Exception");
			return false;
		}
		
	}
	
	/*ֻ��changeGEDataFile����
	 * 
	 * Ҫ��Դ�ļ���ʽΪ��
	 * 	�����ڶ����������м�ѭ��д�Ĳ��֣�������������
	 * 	�ѵ��ظ��İ������˵�
	 * 	�Ѷ�Communicator EXT FLASH Sequence & Status / FLASH Command�İ������˵�
	 * 	�����checksum��ʱ����ǰ��һ������
	 */
	
	public boolean startCracking(File srcFile,File dataFile, String passwd){
		BufferedReader reader = null;
		BufferedReader dataReader = null;
		String response = "";
		try {

			if(!srcFile.exists() || !dataFile.exists() || passwd.length()>10){
				return false;
			}
		
			int passwdLen = 0;
			String lineStr = "";
		
			String preResponse = response;
			//�����ض��Ķ�ĳ���Ĵ����ı��ģ������޸�
			String readPackStr = "|0   |00|50|56|ed|2d|8c|00|0c|29|14|a3|25|08|00|45|00|00|34|d4|3c|40|00|80|06|ef|50|c0|a8|b4|81|c0|a8|01|64|08|c6|01|f6|23|56|40|b7|42|7b|a3|fc|50|18|f9|e1|1c|c4|00|00|0c|17|00|00|00|06|01|03|ff|81|00|01|";
			int transitionID = 1;
			
			//****************�ڶ�����������******************
			reader =new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
			//д����
			lineStr = reader.readLine();
			lineStr = (String) lineStr.subSequence(0, lineStr.length()-30);
			passwdLen = passwd.length();
			for(int i=0;i<10;i++){
				if(i < passwdLen){
					lineStr = lineStr + CommonFunction.changeIntegerToHexStr(1, (int)passwd.charAt(i), "") + "|";
				}else{
					lineStr = lineStr + "20|";
				}
			}
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			transitionID++;
			
			//��FLASH Locked Port ���أ� 01 00��01��port B is in use;00: Port A,I/O is locked in the flash routines������Ҳ�����Ϊʲôһ��ʼ�������������ǿ��ܺ������ͷ�
			lineStr = reader.readLine();
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
				
			if(!(response.equals("0100"))){
				throw new Exception("-1 ����");
			}
			
			//������ȡ��������Ϣ�Ĳ�������һ���Ƕ�FLASH Locked Port���������ظ������˵�
			lineStr = reader.readLine();
			lineStr = reader.readLine();
			lineStr = reader.readLine();
			
			//дPort to Port Communications (65417-65421) Commands to Port B	 ����Ϊ�� 01 01(Reset to FLASH Mode��
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
		
			
			//������ȡ��������Ϣ�Ĳ�������һ���Ƕ�FLASH Locked Port���������ظ������˵�
			lineStr = reader.readLine();
			lineStr = reader.readLine();
			lineStr = reader.readLine();
			
			//��Lock states ���أ� ff ff ff ff ff 00(last one no use)
			lineStr = reader.readLine();
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
			if(!response.equals("ffffffffff00")){
				throw new Exception("-2 ����");
			}
			
			//дPort Control Command ����Ϊ�� 01 02(Lock Port 2 for use)
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
		
			
			//��Lock states ���أ� ff ff 01 ff ff 00(Port 2 is locked by Port 3)
			lineStr = reader.readLine();
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
			if(!response.equals("ffff01ffff00")){
				System.out.println(response);
				throw new Exception("-3 ����");
			}
			
			//дPort Control Command ����Ϊ�� 02 02(unlock Port 2)
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
	
			
			//��Lock states ���أ� ff ff ff ff ff 00(last one no use)
			lineStr = reader.readLine();
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
				
			if(!response.equals("ffffffffff00")){
				System.out.println(response);
				throw new Exception("-4 ����");
			}
			
			//дPort to Port Communications (65417-65421) Commands to Port B	 ����Ϊ�� 00 00(no command)
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
		
			
			//****************��������������******************
			//д����
			lineStr = reader.readLine();
			lineStr = (String) lineStr.subSequence(0, lineStr.length()-30);
			passwdLen = passwd.length();
			for(int i=0;i<10;i++){
				if(i < passwdLen){
					lineStr = lineStr + CommonFunction.changeIntegerToHexStr(1, (int)passwd.charAt(i), "") + "|";
				}else{
					lineStr = lineStr + "20|";
				}
			}
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
			
			//��ip ���� c0 a8 01 64
			lineStr = reader.readLine();
			/*response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			if(!response.equals("c0a80164")){
				System.out.println(response);
				throw new Exception("1 ����");
			}*/
		
			
			//��Enhanced Programmable setting block.����  ff ff ff ff
			lineStr = reader.readLine();
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
				
			if(!response.equals("ffffffff")){
				throw new Exception("2 ����");
			}
			
			
			//��Communicator EXT Operation Indicator.���� 00 00 (running in normal operation)
			lineStr = reader.readLine();
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
				
			if(!response.equals("0000")){
				throw new Exception("3����");
			}
	
			
			//дCommunicator EXT FLASH Sequence & Status / FLASH Command	 ���ݣ�01 01(Reset to FLASH Operation��
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
		
			
			//��Communicator EXT Operation Indicator	 ���� 00 10 ��Requested FLASH Operation by Communication��
			
			Thread.sleep(1000);
			lineStr = reader.readLine();
			while(true){
				response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
				if(response.equals("0010")){
					break;
				}
			}
		
			
			//��Communicator EXT FLASH Sequence & Status / FLASH Command	 ���� 00 00(sequence number:0,last FLASH Operation failed)
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(readPackStr, transitionID++);
			if(!response.equals("0000")){
				throw new Exception("5����");
			}
		
			
			//дCommunicator EXT FLASH Sequence & Status / FLASH Command	 00 00(Lock the FLASH Routines to this Com Port.)
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
			
			//��Communicator EXT FLASH Sequence & Status / FLASH Command	 ���� 01 01(sequence number:1,last FLASH Operation passed)
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(readPackStr, transitionID++);
			if(!response.equals("0101")){
				throw new Exception("6����");
			}
			
			
			//��FLASH Locked Port ���� 01 01(port B in use now, port B is locked for FLASH Routines)
			lineStr = reader.readLine();
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			if(!response.equals("0101")){
				throw new Exception("7����");
			}
			
			
			//дCommunicator EXT FLASH Sequence & Status / FLASH Command	 ���ݣ�00 03(Erase the Programmable Settings Block)
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
			//��Communicator EXT FLASH Sequence & Status / FLASH Command		���� 	02 01(sequence number:2,last FLASH Operation passed)
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(readPackStr, transitionID++);
			if(!response.equals("0201")){
				throw new Exception("8����");
			}
			preResponse = response;
			
			//��ʼ��ʽˢ���� Ҫ��ˢ��ȥ�����ݲ���ֻ����ˢ��ȥ�����ݣ�û�ж��Ĳ���
			dataReader =new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
			String dataStr = dataReader.readLine();
			int dataIndex = 0;
			boolean failed = false;
			while(lineStr != null){
				if(!failed){
					lineStr = reader.readLine();
				}else{
					failed = false;
				}
				//��������͵���
				if(lineStr.equals("")) break;
				//����д���ݰ�
				dataIndex = ((GEConnector)connector).sendWritePackageAndShowResponse(lineStr, dataStr, transitionID++, dataIndex);
				//���Ͷ����ݰ�
				response = ((GEConnector)connector).sendReadPackageAndShowResponse(readPackStr, transitionID++);
				if(response.substring(0, 2).equals(preResponse.substring(0, 2))|| !response.substring(2).equals("01")){
					failed = true;
					dataIndex = dataIndex - 64;
				}else{
					preResponse = response;
				}
				
			}
			dataReader.close();
			
			//дCommunicator EXT FLASH Sequence & Status / FLASH Command ���ݣ� 00 04(Calculate the Programmable Settings Checksum)
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
			//��Communicator EXT FLASH Sequence & Status / FLASH Command	 ���أ�XX 01(sequence number:XX,last FLASH Operation passed)
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(readPackStr, transitionID++);
			if(response.substring(0, 2).equals(preResponse.substring(0, 2))|| !response.substring(2).equals("01")){
				throw new Exception("9����");
			}
			preResponse = response;
			
			//��Communicator EXT FLASH Programmable Settings Checksum ����ֵ��������c6 0e(Programmable Settings Checksum)
			lineStr = reader.readLine();
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			String checkSum = response;
			
			//дCommunicator EXT FLASH Programmable Settings Checksum ���ݣ������� c6 0e(Programmable Settings Checksum)
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr.subSequence(0, lineStr.length()-6)
					+ checkSum.substring(0, 2)+"|"+checkSum.substring(2, 4)+"|", transitionID);
			
			//��Communicator EXT FLASH Sequence & Status / FLASH Command	 ����ֵ��XX 01(sequence number:XX,last FLASH Operation passed)
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(readPackStr, transitionID++);
			if(response.substring(0, 2).equals(preResponse.substring(0, 2))|| !response.substring(2).equals("01")){
				throw new Exception("10����");
			}
			preResponse = response;
			
			//дCommunicator EXT FLASH Sequence & Status / FLASH Command ����Ϊ��01 00(Reset to Normal Operation)	
			lineStr = reader.readLine();
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
			//��0��ʼ�����֡�������Ժ�Ū
			
			reader.close();
			return true;
		}catch(Exception ex){
			Log.i("GE.java:Crack Fail", ex.getMessage()+ "  "+response);
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return false;
			}
			return false;
		}
	}
	
	/**
	 * ���μ�⣬�����������״̬������������룬��ѭ���ȴ�����
	 * @param passwd
	 * @return
	 */
	public boolean monitorAndChangePasswd(String passwd){
		try {
			int passwdLen = passwd.length();
			String response = "";
			int transitionID = 0;
			String lineStr = "|0   |00|50|56|ed|2d|8c|00|0c|29|14|a3|25|08|00|45|00|00|34|40|76|40|00|80|06|83|17|c0|a8|b4|81|c0|a8|01|64|0b|b1|01|f6|39|39|de|ee|44|7a|38|16|50|18|fa|c3|da|82|00|00|00|54|00|00|00|06|01|03|ff|28|00|01|";
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			if(response == null || response.charAt(3) != '4'){
				return false;
			}
		
			//***********packages************
			for(int num = 0; num < 1; num++){
				//************���������**************
				//д��password lock 0x0000,��֪��Ϊʲô��Ҫд0x0000���ܰ�����Ϊ0x0001
				lineStr = "|0   |00|50|56|ed|2d|8c|00|0c|29|14|a3|25|08|00|45|00|00|34|40|7a|40|00|80|06|83|13|c0|a8|b4|81|c0|a8|01|64|0b|b1|01|f6|39|39|df|12|44|7a|38|39|50|18|fa|a0|da|b1|00|00|00|57|00|00|00|06|01|06|ff|2d|00|00|";
				response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
				//��һ��д���뵽new password A
				lineStr = "|0   |00|50|56|ed|2d|8c|00|0c|29|14|a3|25|08|00|45|00|00|45|40|7f|40|00|80|06|82|fd|c0|a8|b4|81|c0|a8|01|64|0b|b1|01|f6|39|39|df|5f|44|7a|38|73|50|18|fa|66|a6|e3|00|00|00|5c|00|00|00|17|01|10|ff|30|00|08|10|20|20|20|20|20|20|41|42|43|20|20|20|20|20|20|20|";
				lineStr = (String) lineStr.subSequence(0, lineStr.length()-30);
				passwdLen = passwd.length();
				for(int i=0;i<10;i++){
					if(i < passwdLen){
						lineStr = lineStr + CommonFunction.changeIntegerToHexStr(1, (int)passwd.charAt(i), "") + "|";
					}else{
						lineStr = lineStr + "20|";
					}
				}
				((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
				//�ڶ���д���뵽new password B
				lineStr = "|0   |00|50|56|ed|2d|8c|00|0c|29|14|a3|25|08|00|45|00|00|45|40|7f|40|00|80|06|82|fd|c0|a8|b4|81|c0|a8|01|64|0b|b1|01|f6|39|39|df|5f|44|7a|38|73|50|18|fa|66|a6|e3|00|00|00|5c|00|00|00|17|01|10|ff|38|00|08|10|20|20|20|20|20|20|41|42|43|20|20|20|20|20|20|20|";
				lineStr = (String) lineStr.subSequence(0, lineStr.length()-30);
				passwdLen = passwd.length();
				for(int i=0;i<10;i++){
					if(i < passwdLen){
						lineStr = lineStr + CommonFunction.changeIntegerToHexStr(1, (int)passwd.charAt(i), "") + "|";
					}else{
						lineStr = lineStr + "20|";
					}
				}
				((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
		
				//д��0x0001��password command
				lineStr = "|0   |00|50|56|ed|2d|8c|00|0c|29|14|a3|25|08|00|45|00|00|34|40|81|40|00|80|06|83|0c|c0|a8|b4|81|c0|a8|01|64|0b|b1|01|f6|39|39|df|99|44|7a|38|8b|50|18|fa|4e|da|20|00|00|00|5e|00|00|00|06|01|06|ff|2f|00|01|";
				response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			
			}
			
		}catch (Exception e) {
			String str = e.getMessage();
		
			return false;
		}
		return true;
	}
	
	public boolean validatePasswd(String passwd){
		try {
			int passwdLen = passwd.length();
			String response = "";
			int transitionID = 0;
			passwdLen = passwd.length();
			
			//write the passwd
			String lineStr = "|0   |00|50|56|ed|2d|8c|00|0c|29|14|a3|25|08|00|45|00|00|45|63|8b|40|00|80|06|5f|f1|c0|a8|b4|81|c0|a8|01|64|0c|1a|01|f6|8a|6b|e0|1b|31|c3|00|e3|50|18|f9|7e|dc|a7|00|00|04|ca|00|00|00|17|01|10|ff|20|00|08|10|20|20|20|20|20|20|4d|59|50|41|53|53|57|4f|52|44|";
			lineStr = (String) lineStr.subSequence(0, lineStr.length()-30);
			
			for(int i=0;i<10;i++){
				if(i < passwdLen){
					lineStr = lineStr + CommonFunction.changeIntegerToHexStr(1, (int)passwd.charAt(i), "") + "|";
				}else{
					lineStr = lineStr + "20|";
				}
			}
			((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			transitionID++;
			
		
			//check if the passwd door is open
			lineStr = "|0   |00|50|56|ed|2d|8c|00|0c|29|14|a3|25|08|00|45|00|00|34|40|76|40|00|80|06|83|17|c0|a8|b4|81|c0|a8|01|64|0b|b1|01|f6|39|39|de|ee|44|7a|38|16|50|18|fa|c3|da|82|00|00|00|54|00|00|00|06|01|03|ff|28|00|01|";
			
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, transitionID++);
			if(response == null || response.charAt(3) != '4'){
				return false;
			}
		}catch(Exception ex){
			return false;
		}
		return true;
	}
	
	/**
	 * get the GE's password protection state
	 * @return
	 * 	true: protected
	 */
	public boolean getProtectState(){
		try{
			String lineStr = "";
			String response = "";
			lineStr = "|0   |00|50|56|ed|2d|8c|00|0c|29|14|a3|25|08|00|45|00|00|34|40|76|40|00|80|06|83|17|c0|a8|b4|81|c0|a8|01|64|0b|b1|01|f6|39|39|de|ee|44|7a|38|16|50|18|fa|c3|da|82|00|00|00|54|00|00|00|06|01|03|ff|28|00|01|";
			
			response = ((GEConnector)connector).sendReadPackageAndShowResponse(lineStr, 0);
			if(response.charAt(3) == '5'){
				return false;
			}
			return true;
		}catch(Exception ex){
			return true;
		}
	}
	
	/**
	 * ��Ϊ���еĵ����������͵�byte�ź�����е�����˳��һ�£�������Ҫת��һ��
	 * @param type 
	 * @param dir:  �����Ǵӱ����е����������ת���������෴
	 * @return
	 */
	public int getCorrespondingConType(int type, boolean dir){
		if(dir){
			switch(type){
			case 4:
				return 0;
			case 0:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			}
		}else{
			switch(type){
			case 0:
				return 4;
			case 1:
				return 0;
			case 2:
				return 2;
			case 3:
				return 3;
			}
		}
		return -1;
		
	}
}
