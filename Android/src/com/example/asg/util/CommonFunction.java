package com.example.asg.util;

import android.app.Activity;
import android.app.AlertDialog;

public class CommonFunction {
		/**
		 * 	���srcNum��byte��Ҫ�Ⱥ�0x00ff��һ���ٴ������� 
		 * @param bitNum
		 * @param srcNum
		 * @param sepChar
		 * @return
		 * 	�����ַ���Ϊ 00 01 0x�� �ָ�����sepCharȷ�������Ϊ�գ�����""��bitNum���ֽ���
		 */
		public static String changeIntegerToHexStr(int bitNum, int srcNum,String sepChar){
			String result = "";
			while(srcNum != 0){
				result = CommonFunction.changeDecimalTo2BitHexStr(srcNum%256) + sepChar + result;
				srcNum = srcNum/256;
			}
			int len = result.length();
			int totalLen = bitNum*(2+sepChar.length());
			while(len < totalLen){
				result = "00" + sepChar + result;
				len = len+2+sepChar.length();
			}
			return result;
		}
		//used by changeIntegerToHexStr
		public static String changeDecimalTo2BitHexStr(int decimal){
			String result = "";
			char[] hex = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
			int tempInt = (decimal&0x0f);
			result = hex[tempInt] + result;
			tempInt = (decimal>>4)&0x0f;
			result = hex[tempInt] + result;
			return result;
		}
		
		/**
		 * 
		 * @param title
		 * @param message
		 */
		public static void showDialog(String title, String message,Activity activity){
			AlertDialog.Builder builder= new AlertDialog.Builder(activity);
	        builder.setTitle(title);
	        builder.setMessage(message);
	        builder.setPositiveButton("Yes", null);
	        builder.create().show();
		}
		
		/**
		 * change the byte array to ascii String
		 * @param b 	the byte Array
		 * @param len	the length
		 * @return
		 */
		public static String getASCStr(byte[] b,int len){
			char c;
			String result = "";
			for(int i=0;i<len;i++){
				c = (char) b[i];
				result = result+c;
				//System.out.print(c+" ");
			}
			return result;
			
		}
		
		/**
		 * 
		 * @param hex
		 * @return
		 */
		public static int changeHexStrTobyte(String hex){
			char first = hex.charAt(0);
			char second = hex.charAt(1);
			int result = 0;
			if(first >= '0' && first <= '9'){
				result =  (result + (first-'0'));
			}else{
				result =  (result + (first-'a') + 10);
			}
			if(second >= '0' && second <= '9'){
				result =  (result * 16 + (second-'0'));
			}else{
				result = (result * 16 + (second-'a') + 10);
			}
			return result;
		}
		
		/**
		 * ע��hex�ĳ��ȱ�����ż�����Ҳ����������ַ�
		 * @param hex
		 * @return
		 */
		public static int changeHexStrToInteger(String hex){
			int len = hex.length();
			int result = 0;
			for(int i=0;i<len;){
				result = result*256 + changeHexStrTobyte(hex.substring(0, 2));
				hex = hex.substring(2);
				i = i+2;
			}
			return result;
			
		}
}
