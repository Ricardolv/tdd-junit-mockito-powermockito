package com.richard.tdd.matchers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.richard.tdd.utils.DataUtils;

public class DataDiferencaDiasMatcher extends TypeSafeMatcher<Date> {
	
	private Integer qtdDias;

	public DataDiferencaDiasMatcher(Integer qtdDias) {
		this.qtdDias = qtdDias;
	}

	@Override
	public void describeTo(Description desc) {
		Date dataEsperada = DataUtils.obterDataComDiferencaDias(this.qtdDias);
		DateFormat formart = new SimpleDateFormat("dd/MM/YYYY");
		desc.appendText(formart.format(dataEsperada));
	}

	@Override
	protected boolean matchesSafely(Date data) {
		return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(this.qtdDias));
	}

}
