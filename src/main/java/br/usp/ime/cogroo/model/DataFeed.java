package br.usp.ime.cogroo.model;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import br.com.caelum.vraptor.ioc.Component;
import br.usp.ime.cogroo.logic.AnalyticsManager;

import com.google.api.client.util.Key;

/**
 * 
 * @author Michel
 * 
 */
@Component
public class DataFeed {
	@Key
	public String title;
	@Key
	public String id;
	@Key("openSearch:totalResults")
	public Integer totalResults;
	@Key("openSearch:startIndex")
	public Integer startIndex;
	@Key("openSearch:itemsPerPage")
	public Integer itemsPerPage;
	@Key("dxp:startDate")
	public String startDate;
	@Key("dxp:endDate")
	public String endDate;
	@Key("dxp:aggregates")
	public Aggregates aggregates;
	@Key("dxp:dataSource")
	public List<Source> dataSource;
	// @Key("dxp:segment") public String segment;
	@Key("entry")
	public List<DataEntry> answers;

	public Date getStartDate() {
		try {
			return AnalyticsManager.DATE_FORMAT.parse(startDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Date getEndDate() {
		try {
			return AnalyticsManager.DATE_FORMAT.parse(endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static class Aggregates {
		@Key("dxp:metric")
		public List<Metric> metrics;
	}

	public static class Metric {
		@Key("@name")
		public String name;
		@Key("@value")
		public Integer value;
		@Key("@confidenceInterval")
		public Float confidenceInterval;
		@Key("@type")
		public String type;
	}

	public static class Source {
		@Key("dxp:tableId")
		public String tableId;
		@Key("dxp:tableName")
		public String tableName;
		@Key("dxp:property name=ga:profileId")
		public String profileId;
		@Key("dxp:property name=ga:webPropertyId")
		public String webPropertyId;
		@Key("dxp:property name=ga:accountName")
		public String accountName;
	}

	public static class DataEntry {
		@Key
		public String title;
		@Key("dxp:dimension")
		public List<Dimension> dimensions;
		@Key("dxp:metric")
		public List<Metric> metrics;
	}

	public static class Dimension {
		@Key
		public String name;
		@Key
		public String value;
	}
}
