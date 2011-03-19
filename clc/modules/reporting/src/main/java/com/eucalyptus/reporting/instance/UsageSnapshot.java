package com.eucalyptus.reporting.instance;

/**
 * UsageSnapshot of the resource usage of some instance at some point in time.
 * Contains <i>cumulative</i> usage data so it's populated with
 * all resource usage which has occurred up until this snapshot was
 * instantiated. 
 * 
 * @author tom.werges
 */
class UsageSnapshot
{
	private final Long timestampMs;
	private final UsageData cumulativeUsageData;

	/**
	 * For hibernate usage only; don't extend this class
	 */
	protected UsageSnapshot()
	{
		this.timestampMs = null;
		this.cumulativeUsageData = null;
	}

	public UsageSnapshot(long timestampMs, UsageData cumulativeUsageData)
	{
		this.timestampMs = new Long(timestampMs);
		this.cumulativeUsageData = cumulativeUsageData;
	}

	public long getTimestampMs()
	{
		assert this.timestampMs != null;
		return this.timestampMs.longValue();
	}

	public UsageData getCumulativeUsageData()
	{
		return this.cumulativeUsageData;
	}

}
