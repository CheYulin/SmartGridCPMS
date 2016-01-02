package com.example.asg.services.meteraccess;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

import com.example.asg.util.CommonFunction;
import com.example.asg.view.ConnectingActivity;

import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersResponse;
import net.wimpi.modbus.net.TCPMasterConnection;

public class GEConnector extends com.example.asg.services.meteraccess.Connector{
	
	
	public String getF7Data(int a, int b, int num, int PTN, int PTD, int CTN, int CTD) {
		/*if(!this.checkConAndRepair()){
			return "";
		}*/
		
		double temp = 0;

		req = new ReadMultipleRegistersRequest(a, b);
		req.setUnitID(this.unitID);
		trans = new ModbusTCPTransaction(con);

		trans.setRequest(req);
		try {
			trans.execute();
		} catch (ModbusIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusSlaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		res = (ReadMultipleRegistersResponse) trans.getResponse();

		int ls = ((res.getRegisterValue(0) & 0xffff) << 16 | (res.getRegisterValue(1) & 0xffff));

		switch (num) {
		case 1:
			temp = (double) ls / 65536 * (PTN / PTD);
			//conAct.tot1 += temp;
			break;
		case 2:
			temp = (double) ls / 65536 * (PTN / PTD);
			//conAct.tot2 += temp;
			break;
		case 3:
			temp = (double) ls / 65536 * (CTN / CTD);
			//conAct.tot3 += temp;
			break;
		case 4:
			temp = (double) ls / 65536 * (PTN / PTD) * (CTN / CTD);
			//conAct.tot4 += temp;
			break;
		}
		String str = String.valueOf(temp);
		if (str.length() <= 11)
			return str;
		else
			return str.substring(0, 10);
	}
	
	/**
	 * ������Ϊд�Ͷ�����������������˷��ص����ݵ���ʼλ�ò�ͬ
	 * @param lineStr
	 * @param transitionID
	 * @return
	 */
	public String sendReadPackageAndShowResponse(String lineStr, int transitionID){
		//�Լ������İ���0x0036��ʼ 6+(48+6)*3=168
		String result = "";
		try{
			
			if(lineStr == null) return "";
			lineStr = lineStr.substring(174);
	
			//��transitionID��ӵ�����ͷ��
			lineStr = CommonFunction.changeIntegerToHexStr(2, transitionID,"|") + lineStr;
			Log.i("GEConnector.sendReadPackageAndshowResponse", "���͵ı���Ϊ��"+lineStr);
			int lineStrLen = lineStr.length();
			byte[] b = new byte[lineStrLen];
			int bIndex = 0;
			ResponsePackage response = null;
			//��00|00|��ʽ�ı����ַ���ת����byte[]
			for(int i=0;i<lineStrLen;){
				b[bIndex++] = (byte) CommonFunction.changeHexStrToInteger(lineStr.substring(i, i+2));
				i = i+3;
			}
		
			response = sendPackageAndGetResponseNoBlock(b, bIndex);
			
			if(response.data == null){
				return "";
			}else{

				for (int i=9;i<response.packageLen;i++) {
					//if(i >= 9){
			
					result = result + CommonFunction.changeIntegerToHexStr(1, response.data[i]&0x00ff, "");
					//}
				}
				
			}
			
		}catch(Exception ex){
			Log.i("GEConnector.sendReadPackageAndShowResponse", "exception in sendReadPackageAndShowResponse");
			ex.printStackTrace();
			return "";
			//System.exit(1);
		}
		return result;	
	}

	/**
	 * only used for send the data read back to meter
	 * @param lineStr
	 * @param dataStr
	 * @param transitionID
	 * @param index
	 * @return
	 */
	public int sendWritePackageAndShowResponse(String lineStr, String dataStr, int transitionID, int index){
		//�Լ������İ���0x0036��ʼ 6+(48+8)*3=168,��transition identification֮��ʼ
		try{
			if(lineStr == null || lineStr.equals("")) return -1;
			lineStr = lineStr.substring(174);
			lineStr = CommonFunction.changeIntegerToHexStr(2, transitionID,"|") + lineStr;
			int lineStrLen = lineStr.length();
			
			byte[] b = new byte[lineStrLen];
			ResponsePackage response = null;
			
			//��һ���Ȱѵ������֮ǰ�İ�����ת��
			int bIndex = 0;
			//45�ǰ��������ݵ�ǰ4���ֽڵ�
			for(int i=0;i<51;){
				b[bIndex++] = (byte) CommonFunction.changeHexStrToInteger(lineStr.substring(i, i+2));
				i = i+3;
			}
			
			//�ڶ����ѵ����������
			byte data = 0;
			for(int i=0;i<64;i++){
				data = (byte) CommonFunction.changeHexStrToInteger(dataStr.substring((index+i)*2, (index+i)*2+2));
				b[bIndex++] = data;
			}
			
			//��������ÿһ�н�β��00 01����
			b[bIndex++] = 00;
			b[bIndex++] = 01;
			
			response = sendPackageAndGetResponseNoBlock(b, bIndex);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		return index+64;

	}
}
