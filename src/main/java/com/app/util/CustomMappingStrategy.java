package com.app.util;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.app.annotations.CsvMultipleDataMaxSize;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
	private T bean;

	/**
	 * ヘッダーを生成
	 * @param bean データクラス
	 * @return 生成したヘッダー
	 */
	@Override
	public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
		super.generateHeader(bean);
		this.bean = bean;
		// データクラス(T bean)のフィールドの数
		int fieldSize = super.getFieldMap().values().size();

		// ヘッダー情報の初期化
		List<String> headerList = new LinkedList<String>();

		for (int index = 0; index < fieldSize; index++) {
			// データクラス(T bean)のフィールドを取得
			BeanField<T, Integer> beanField = super.findField(index);

			if (this.checkFieldInfoNotEmpty(beanField)) {
				this.setColumnHeader(beanField, headerList);
			} else {
				headerList.add(StringUtils.EMPTY);
			}
		}

		return headerList.toArray(new String[headerList.size()]);
	}

	/**
	 * ヘッダー情報をセットする
	 * @param beanField フィールド情報
	 * @param headerList ヘッダーリスト設定変数
	 */
	private void setColumnHeader(BeanField<?, ?> beanField, List<String> headerList) {
		if (this.isMultipleData(beanField)) {
			// 配列やリストの場合
			int maxSize = beanField.getField().getDeclaredAnnotationsByType(CsvMultipleDataMaxSize.class)[0].maxSize();

			for (int index = 1; index <= maxSize; index++) {
				StringBuilder sb = new StringBuilder();
				// header名
				sb.append(this.getHeaderName(beanField));
				// index
				sb.append(Integer.toString(index));

				headerList.add(sb.toString());
			}
		} else {
			// 単体のフィールドの場合はそのままヘッダー名リストに追加
			headerList.add(this.getHeaderName(beanField));
		}
	}

	/**
	 * フィールドががリストや配列かどうか判定
	 * @param beanField 対象のフィールド
	 * @return 判定結果
	 */
	private boolean isMultipleData(BeanField<?, ?> beanField) {

		Field field = beanField.getField();
		field.setAccessible(true);
		try {
			if (field.getType().isArray() || field.get(this.bean) instanceof java.util.List) {
				// フィールドが配列またはリストの場合
				return true;
			} else {
				return false;
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * フィールド情報のNullチェック
	 * @param beanField
	 * @return 判定結果 
	 */
	private boolean checkFieldInfoNotEmpty(BeanField<?, ?> beanField) {
		return ObjectUtils.isNotEmpty(beanField)
				&& ObjectUtils.isNotEmpty(beanField.getField())
				// データクラスで宣言されたアノテーションの情報
				&& ObjectUtils.isNotEmpty(beanField.getField().getDeclaredAnnotationsByType(CsvBindByName.class));
	}

	/**
	 * フィールド情報からカラム名を取得
	 * @param beanField フィールド情報 
	 * @return 対象のカラム名
	 */
	private String getHeaderName(BeanField<?, ?> beanField) {
		return beanField.getField().getDeclaredAnnotationsByType(CsvBindByName.class)[0].column();
	}
	
	/**
	 * データを生成
	 * @param bean データクラス
	 * @return 1行分の書き込みデータ
	 */
	@Override
	public String[] transmuteBean(T bean) throws CsvFieldAssignmentException, CsvChainedException {
		// データクラス(T bean)のフィールドの数
		int headerSize = headerIndex.findMaxIndex() + 1;

		// 書き込み情報の初期化
		List<String> writeDataList = new LinkedList<String>();

		for (int index = 0; index < headerSize; index++) {
			// データクラス(T bean)のフィールドを取得
			BeanField<T, Integer> beanField = super.findField(index);
			if (this.isMultipleData(beanField)) {
				// 配列やリストの場合
				
				// 複数データの初期化変数
				String[] multipleData = null;
				
				Field field = beanField.getField();
				if (field.getType().isArray()) {
					// フィールドが配列の場合
					multipleData = (String[]) beanField.getFieldValue(bean);
					for (String data : multipleData) {
						writeDataList.add(data);
					}
					continue;
				}

				try {
					if (field.get(bean) instanceof java.util.List) {
						// フィールドがリストの場合
						multipleData = this.fieldValueListConvertToStringArray(beanField.getFieldValue(bean));

						for (String data : multipleData) {
							writeDataList.add(data);
						}
						continue;
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}

			} else {
				writeDataList.add(
						ObjectUtils.isNotEmpty(beanField.getFieldValue(bean)) ? beanField.getFieldValue(bean).toString()
								: StringUtils.EMPTY);
			}
		}

		return writeDataList.toArray(new String[writeDataList.size()]);
	}
	
	/**
	 * フィールドがリストの場合以下の形式でデータなので「[」「]」「 」(空白)で置換して、「,」でsplitする。
	 * データ形式 : [?, ?, ?, ?, ?, ...]
	 * 
	 * @param fieldValue フィールドのバリュー
	 * @return String[]に変換した値
	 */
	private String[] fieldValueListConvertToStringArray(Object fieldValue) {
		// 「[]」「 」消して、「,」でsplitする
		return fieldValue.toString().replaceAll("\\[", StringUtils.EMPTY).replaceAll("\\]", StringUtils.EMPTY)
				.replaceAll(" ", StringUtils.EMPTY)
				.split(",");
	}
}
