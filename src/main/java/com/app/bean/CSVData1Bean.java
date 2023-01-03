package com.app.bean;

import java.util.List;

import com.app.annotations.CsvMultipleDataMaxSize;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CSVData1Bean {
	@CsvBindByPosition(position = 0)
	@CsvBindByName(column = "エリア", required = true)
	private String area;
	@CsvBindByPosition(position = 1)
	@CsvBindByName(column = "ID", required = true)
	private String id;
	@CsvBindByPosition(position = 2)
	@CsvBindByName(column = "年月日", required = true)
	private String nenTukiHi;
	
	@CsvBindByPosition(position = 3)
	@CsvBindByName(column = "listData", required = true)
	@CsvMultipleDataMaxSize(maxSize = 3)
	private List<String> listData;
	
	@CsvBindByPosition(position = 4)
	@CsvBindByName(column = "arrayData", required = true)
	@CsvMultipleDataMaxSize(maxSize = 3)
	private String[] arrayData;
}
