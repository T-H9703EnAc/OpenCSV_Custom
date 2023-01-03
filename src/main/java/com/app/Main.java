package com.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.app.bean.CSVData1Bean;
import com.app.util.CSVUtil;

public class Main {

	public static void main(String[] args) throws Exception {

		CSVUtil<CSVData1Bean> util = new CSVUtil<CSVData1Bean>();

		String[] array = new String[3];

		for (int i = 0; i < array.length; i++) {
			array[i] = Integer.toString(i);
		}

		CSVData1Bean bean = CSVData1Bean
				.builder()
				.areaNo("001")
				.kyotenId("001")
				.nenTukiHi("20220101")
				.vData(Arrays.asList(array))
				.array(array)
				.build();
		System.out.println(Arrays.toString(util.getHeaderField(bean, CSVData1Bean.class)));

		String path = ".\\src\\main\\resources\\csv\\result.csv";

		List<CSVData1Bean> list = new ArrayList<CSVData1Bean>() {
			{
				add(bean);
			}
		};

		util.writeCSV(list, CSVData1Bean.class, path);

	}

}
