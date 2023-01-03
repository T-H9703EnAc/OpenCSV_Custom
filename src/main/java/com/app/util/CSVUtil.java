package com.app.util;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class CSVUtil<T> {
	
	/**
	 * データクラスに設定したヘッダーの取得
	 * @param bean データクラス 
	 * @param type データクラスの型
	 * @return ヘッダー名
	 * @throws CsvRequiredFieldEmptyException
	 */
	public  String[] getHeaderField(T bean, Class<? extends T> type) throws CsvRequiredFieldEmptyException {
		CustomMappingStrategy<T> mappingStrategy = new CustomMappingStrategy<>();
        mappingStrategy.setType(type);      
        return mappingStrategy.generateHeader(bean);
	}
	
	 /**
     * CSVファイルを出力する
     * @param beanList 書き込むデータリスト
     * @param bean JavaBeansのクラス
     * @param path CSVファイルのパス
     * @throws Exception
     */
    public void writeCSV(List<T> beanList, Class<T> bean, String path) throws Exception {
        File file = new File(path); 
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
            CustomMappingStrategy<T> mappingStrategy = new CustomMappingStrategy<>();
            mappingStrategy.setType(bean);
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(csvWriter)       
            .withMappingStrategy(mappingStrategy)
            .build();
            beanToCsv.write(beanList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }     
    }

}
