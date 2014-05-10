package com.ederbase.model;

//Additional checks
//SELECT * FROM Schedule where SchLaborCategories != '' and (SchFlags & 0x10) = 0;
//SELECT * FROM Schedule where SchLaborCategories = '' and (SchFlags & 0x10) != 0;
//SELECT * FROM Schedule where SchDependencies = '' and (SchFlags & 0x10) != 0; Starting poing
//SELECT * FROM Calendar where dtStartDay >= curdate() and dtStartDay < ADDDATE(curdate(), INTERVAL 10 year) and (nmDivision=1 or nmUser=123) and (nmFlags&1) != 0;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;

import com.eps.model.EbUserData;

public class EpsXlsProject // extends EpsUserData
{

	protected ResultSet rsProject = null;
	public int nmBaseline = 0;
	protected String stError = "";
	private EbUserData epsUd = null;
	private int iSubmit = 0;
	private ResultSet rsFields = null;
	private int iMaxFields = 0;
	public String stPk = "";
	private String stGoBack = "";
	private int iAnalyzeStatus = 0;
	private long startTime = 0;
	private int iLastSqlCount = 0;
	private EpsCriticalPath[] aCp = null;
	private int[][] aaPath = null;
	int iMaxPath = 0;
	int iPathPosition = 0;
	private String stCommTrace = null;
	private String stHolidays = "";
	private int[] aWeekend = null;
	private Calendar dtProjectStart = null;
	private int iCpIndexByDate = -1;
	private int iMaxRecId = 0;

	private EbDatabase ebdb = null;
	private EbDatabase ebdy = null;

	public ResultSet rsMyDiv = null;

	public EpsXlsProject() {

		this.ebdb = new EbDatabase(0, "localhost", "root", "", "ebeps", "");
		this.ebdy = new EbDatabase(0, "localhost", "root", "", "dbeps", "");
	}

	public void setEpsXlsProject(EbUserData epsUd) {

		this.epsUd = epsUd;
		this.ebdb = new EbDatabase(0, "localhost", "root", "", "ebeps", "");
		this.ebdy = new EbDatabase(0, "localhost", "root", "", "dbeps", "");

		this.startTime = System.nanoTime();

		int iUserId = epsUd.getNmUserId();

		try {
			if (iUserId > 0) {
				rsMyDiv = this.ebdy
						.ExecuteSql("SELECT d.*,o.* FROM Options o"
								+ " LEFT JOIN teb_division d ON d.nmDivision=1 "
								+ " LEFT JOIN teb_refdivision rd ON rd.nmRefType=42 and rd.nmRefId="
								+ iUserId
								+ " and (rd.nmFlags & 1) = 1 and rd.nmDivision=d.nmDivision"
								+ " WHERE o.RecId=1");
			} else {
				rsMyDiv = this.ebdy
						.ExecuteSql("SELECT d.*,o.* FROM Options o LEFT JOIN teb_division d ON d.nmDivision=1 WHERE o.RecId=1");
			}
			rsMyDiv.absolute(1);
		} catch (Exception e) {
			this.stError += "<BR>ERROR: setUser " + e;
		}

		try {
			this.epsUd.setNmHoursPerDay(rsMyDiv.getDouble("nmHoursPerDay"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getError() {
		return this.stError;
	}

	public String processCriticalPath() {
		// http://hspm.sph.sc.edu/courses/j716/cpm/cpm.html
		int iMaxSchedule = 0;
		String stHead = "<tr><th valign=top>Critical Path</th>";
		StringBuilder sbReturn = new StringBuilder(5000);
		try {
			this.rsProject = this.ebdy
					.ExecuteSql("select * from Projects where RecId=" + stPk);
			this.rsProject.absolute(1);
			nmBaseline = this.rsProject.getInt("CurrentBaseline");

			// Reset CP flags
			this.ebdy
					.ExecuteUpdate("update Schedule set SchFlags = ( SchFlags & ~0xE00) "
							+ "where  nmProjectId="
							+ stPk
							+ " and nmBaseline="
							+ this.nmBaseline);

			String stWorkdays = this.ebdy
					.ExecuteSql1("select stWorkDays from teb_division where nmDivision="
							+ this.rsProject.getInt("nmDivision"));
			if (stWorkdays == null) {
				stWorkdays = "mo,tu,we,th,fr";
			}
			stWorkdays = stWorkdays.toLowerCase();
			String[] stWork = stWorkdays.split(",");
			aWeekend = new int[7 - stWork.length];
			int iW = 0;
			if (!stWorkdays.contains("sa"))
				aWeekend[iW++] = Calendar.SATURDAY;
			if (!stWorkdays.contains("su"))
				aWeekend[iW++] = Calendar.SUNDAY;
			if (!stWorkdays.contains("mo"))
				aWeekend[iW++] = Calendar.MONDAY;
			if (!stWorkdays.contains("tu"))
				aWeekend[iW++] = Calendar.TUESDAY;
			if (!stWorkdays.contains("we"))
				aWeekend[iW++] = Calendar.WEDNESDAY;
			if (!stWorkdays.contains("th"))
				aWeekend[iW++] = Calendar.THURSDAY;
			if (!stWorkdays.contains("fr"))
				aWeekend[iW++] = Calendar.FRIDAY;

			stHolidays = this.ebdy
					.ExecuteSql1("SELECT GROUP_CONCAT(dtStartDay) FROM Calendar where nmDivision="
							+ this.rsProject.getInt("nmDivision")
							+ " and nmFlags=1 and dtStartDay >= curdate() limit 500");

			// / sbReturn.append( stHead +
			// "<td valign=top>by Estimated Effort</td><td colspan=3 valign=top>";
			iMaxRecId = this.ebdy
					.ExecuteSql1n("SELECT max(RecId) FROM Schedule where nmProjectId="
							+ stPk + " and nmBaseline=" + this.nmBaseline);
			iMaxRecId++;
			aaPath = new int[iMaxRecId][];
			aCp = new EpsCriticalPath[iMaxRecId];
			for (int i = 0; i < aCp.length; i++)
				aCp[i] = null;

			String stSql = "SELECT * FROM schedule s"
					+ " LEFT JOIN teb_link l ON s.nmProjectId=l.nmFromProject AND s.nmBaseline=l.nmFromBaseline"
					+ " AND l.nmLinkFlags=2 AND l.nmFromId=s.RecId"
					+ " WHERE ((s.nmProjectId=" + stPk + " and s.nmBaseline="
					+ this.nmBaseline + ") OR (l.nmToProject=" + stPk
					+ " and l.nmToBaseline=" + this.nmBaseline
					+ ")) AND (s.SchFlags & 0x10) != 0 "
					+ "ORDER BY RecId, nmToId";

			ResultSet rsAll = this.ebdy.ExecuteSql(stSql);
			rsAll.last();
			iMaxSchedule = rsAll.getRow();
			if (iMaxSchedule > 0) {
				// Build the Memory Array
				// for (int iR = 1; iR <= iMaxSchedule && this.stError.length()
				// <= 0;
				// iR++)
				for (int iR = 1; iR <= iMaxSchedule; iR++) {
					rsAll.absolute(iR);
					EpsCriticalPath epsCp = aCp[rsAll.getInt("RecId")];
					if (epsCp == null) {
						epsCp = new EpsCriticalPath();
						epsCp.setEpsCriticalPath(iMaxRecId, epsUd, rsProject,
								rsAll, stHolidays, aWeekend);
						epsCp.addSuccessor(rsAll);
						if (!stPk.equals(rsAll.getString("nmProjectId"))) {
							epsCp.dtStart = Calendar.getInstance();
							epsCp.dtStart
									.setTime(rsAll.getDate("SchStartDate"));
							epsCp.dtEnd = Calendar.getInstance();
							epsCp.dtEnd.setTime(rsAll.getDate("SchFinishDate"));
						}
					} else {
						epsCp.addSuccessor(rsAll);
					}
					aCp[rsAll.getInt("RecId")] = epsCp;
					this.stError += epsCp.getError();
				}
				for (int iR = 0; iR < aCp.length; iR++)
					setDependendies(iR);

				stSql = "select * from Schedule s left join teb_link l on s.nmProjectId=l.nmToProject and "
						+ "s.nmBaseline=l.nmToBaseline and l.nmLinkFlags=2 and l.nmToId=s.RecId"
						+ " where s.nmProjectId="
						+ stPk
						+ " and s.nmBaseline="
						+ this.nmBaseline
						+ " and (s.SchFlags & 0x10 ) != 0 "
						+ "and l.nmFromId is null";
				ResultSet rs = this.ebdy.ExecuteSql(stSql);
				rs.last();
				int iMax = rs.getRow();
				if (iMax > 0) {
					for (int iR = 1; iR <= iMax; iR++) {
						rs.absolute(iR);
						if (aCp[rs.getInt("RecId")] != null) {
							aCp[rs.getInt("RecId")].iFLags = 1; // Starting
																// point.
						} else {
							this.stError += "<BR>ERROR Schedule should NOT be in queue "
									+ rs.getInt("RecId");
						}
						this.stError += aCp[rs.getInt("RecId")].getError();
					}
				} else {
					this.iAnalyzeStatus |= 0x80; // No Starting point for
													// Critical path
				}

				findPaths(0);

//				stCommTrace = this.ebEnt.ebUd.request.getParameter("commtrace");
//				if (stCommTrace != null && stCommTrace.length() > 0
//						&& stCommTrace.equals("t")) // Trace
//				{
//					for (int i = 0; i < aCp.length; i++) {
//						if (aCp[i] != null) {
//							sbReturn.append("<br>" + i + "  S: ");
//							for (int i2 = 0; i2 < aCp[i].aSuccessor.length
//									&& aCp[i].aSuccessor[i2] > 0; i2++) {
//								sbReturn.append(aCp[i].aSuccessor[i2] + ", ");
//							}
//							sbReturn.append("  D: ");
//							for (int i2 = 0; i2 < aCp[i].aDependency.length
//									&& aCp[i].aDependency[i2] > 0; i2++) {
//								sbReturn.append(aCp[i].aDependency[i2] + ", ");
//							}
//						}
//					}
//				}

				String[] aEndDate = new String[iMaxPath];

				sbReturn.append(stHead);
				sbReturn.append("<td valign=top>by End Date</td><td valign=top colspan=3>");
				sbReturn.append("<table border=5>");

				// First, process Critical path to end, but need to stop at each
				// Task
				// with dependenciies
				String stStartDate = this.rsProject
						.getString("FixedProjectStartDate");
				this.dtProjectStart = null;
				// First Task in CP
				if (stStartDate != null && stStartDate.length() > 8) {
					dtProjectStart = EpsStatic.getCalendar(stStartDate);
					if (EpsStatic.daysBetween(null, dtProjectStart) < 0) {
						stError += "<br>ERROR: Invalid start date for project: "
								+ this.rsProject.getString("ProjectName")
								+ " "
								+ stStartDate;
						dtProjectStart = null; // cant be in future
					}
				} else
					dtProjectStart = null;

				Calendar dtEnd = dtProjectStart;
				Calendar dtCpLongest = null;

				// Do all PATHS - Calculate CRITICAL PATH by latest end date
				for (int iI = 0; iI < iMaxPath; iI++) {
					int iPrev = 0;
					for (int iS = 0; iS < iMaxRecId; iS++) {
						if (this.aaPath[iI][iS] > 0) {
							dtEnd = calcStartEnd(this.aaPath[iI][iS],
									dtProjectStart, iPrev);
							iPrev = this.aaPath[iI][iS];
						} else
							break;
					}
					aEndDate[iI] = EpsStatic.getDate(dtEnd);
				}
				// Verify all done
				for (int iP = 0; iP < aCp.length; iP++) {
					if (aCp[iP] != null) {
						if ((aCp[iP].iFLags & 0x8000) == 0)
							stError += "<BR>ERROR: Task not processed in CP by END DATE "
									+ iP;
					}
				}

				// Show Results
				String stBg = "";
				int iSlackLowest = 1000000;
				for (int iC = 0; iC < this.iMaxPath; iC++) {
					sbReturn.append("<tr>");
					stBg = "";
					if (iC == iCpIndexByDate)
						stBg = " bgcolor=yellow ";

					sbReturn.append("<td " + stBg + ">");
					Calendar dtEnd2 = null;
					int iTotalSlack = 0;
					for (int iS = 0; iS < iMaxRecId; iS++) {
						if (this.aaPath[iC][iS] > 0) {
							int iWorkDays = 0;
							aCp[this.aaPath[iC][iS]].iSlack = 0;
							if (dtEnd2 != null)
								iWorkDays = aCp[this.aaPath[iC][iS]]
										.workDaysBetween(
												dtEnd2,
												aCp[this.aaPath[iC][iS]].dtStart);
							if (iWorkDays != 1 && iWorkDays != 0) {
								if (iWorkDays > 0) {
									if (iS > 0
											&& aCp[this.aaPath[iC][iS - 1]] != null)
										aCp[this.aaPath[iC][iS - 1]].iSlack = iWorkDays;
									iTotalSlack += iWorkDays;
								}
								sbReturn.append("<span STYLE='background-color: #aaaaaa'>");
							} else
								sbReturn.append("<span>");
							sbReturn.append(this.aaPath[iC][iS] + " ");
							sbReturn.append(aCp[this.aaPath[iC][iS]].stTitle
									+ " ");
							sbReturn.append(aCp[this.aaPath[iC][iS]]
									.getStartEnd()
									+ "/<font color=blue>"
									+ aCp[this.aaPath[iC][iS]].iWeekend
									+ "</font>/<font color=red>"
									+ aCp[this.aaPath[iC][iS]].iHoliday
									+ "/"
									+ aCp[this.aaPath[iC][iS]].dLag + "</font>");
							if (iWorkDays != 1 && iWorkDays != 0)
								sbReturn.append(" [" + iWorkDays + "]");
							sbReturn.append("</span><br>");
							dtEnd2 = aCp[this.aaPath[iC][iS]].dtEnd;
						} else
							break;
					}
					sbReturn.append(" Total Slack: " + iTotalSlack + " Idx: "
							+ iC);
					if (dtCpLongest == null || dtEnd2.after(dtCpLongest)) {
						dtCpLongest = (Calendar) dtEnd2.clone();
						iCpIndexByDate = iC;
						iSlackLowest = 10000000;
					} else if (dtEnd2.equals(dtCpLongest)) {
						if (iTotalSlack < iSlackLowest) {
							iSlackLowest = iTotalSlack;
							iCpIndexByDate = iC;
						}
					}
					sbReturn.append("</td>");

					sbReturn.append("<td " + stBg + " align=right valign=top>"
							+ aEndDate[iC] + "</td>");
					if (stBg.length() > 0)
						sbReturn.append("<td " + stBg
								+ " align=right valign=top>CRITICAL</td>");
					else
						sbReturn.append("<td " + stBg
								+ " align=right valign=top>&nbsp;</td>");
					sbReturn.append("</tr>");
				}
				sbReturn.append("</table>");
				sbReturn.append("<br>CP: " + iCpIndexByDate);
				sbReturn.append("<table><tr><td class=small><u><b>Legend:</b> (fields from left to right)</u>");
				sbReturn.append("<br>Task Id");
				sbReturn.append("<br>Task Title");
				sbReturn.append("<br>{ Effort in Hours / Start Date: strating Hour / End Date: Ending Hour }");
				sbReturn.append("<br><b>Start - End Date</b>");
				sbReturn.append("<br>(# calendar days between start/end dates)");
				sbReturn.append("<br><font color=blue>/BLUE: # Weekend Days</font>");
				sbReturn.append("<br><font color=red>/RED: # Holidays");
				sbReturn.append("<br>/RED: # Lag</font>");
				sbReturn.append("<br><span STYLE='background-color: #aaaaaa'>[Difference in days from Line above <b>if not 1 day</b>. Shows in gray background]</span>");
				sbReturn.append("</font></td></tr></table></td>");
				sbReturn.append(this.getElapsed());

				// Save everything to DB
				// 1) Mark CP
				String stCp = "";
				for (int iS = 0; iS < iMaxRecId; iS++) {
					if (this.aaPath[iCpIndexByDate][iS] > 0) {
						if (stCp.length() > 0)
							stCp += ",";
						stCp += this.aaPath[iCpIndexByDate][iS];
					}
				}
				this.ebdy
						.ExecuteUpdate("update Schedule set SchFlags = (SchFlags | 0x200) "
								+ "where RecId in ("
								+ stCp
								+ ") and nmProjectId="
								+ stPk
								+ " and nmBaseline="
								+ this.nmBaseline
								+ " and (SchFlags & 0x1000) = 0");
				for (int iC = 0; iC < aCp.length; iC++) {
					if (aCp[iC] != null) {
						stSql = "update Schedule set SchSlack= "
								+ aCp[iC].iSlack + ",SchStartDate=\""
								+ aCp[iC].getStart() + "\""
								+ ",SchFinishDate=\"" + aCp[iC].getEnd() + "\""
								+ "  where RecId = " + iC + " and nmProjectId="
								+ stPk + " and nmBaseline=" + this.nmBaseline;
						this.ebdy.ExecuteUpdate(stSql);
						updateParentEfforts(
								stPk,
								nmBaseline,
								this.ebdy
										.ExecuteSql1n("SELECT SchParentRecId FROM Schedule WHERE RecId = "
												+ iC
												+ " AND nmProjectId="
												+ stPk
												+ " AND nmBaseline="
												+ this.nmBaseline), true);
					}
				}
			} else {
				this.iAnalyzeStatus |= 0x100; // No Schedules for Critical path
			}
		} catch (Exception e) {
			this.stError += "<br>ERROR processCriticalPath ProjectId=" + stPk
					+ " " + e;
			e.printStackTrace();
		}
		return sbReturn.toString();
	}

	public String getElapsed() {
		StringBuilder sbReturn = new StringBuilder(5000);
		long endTime = System.nanoTime();
		// sbReturn.append("<td valign=top>"
		// + (ebEnt.dbDyn.getSqlCount() - iLastSqlCount) + "</td>");
		// long elapsedTime = endTime - startTime;
		// double seconds = elapsedTime / 1.0E09;
		// sbReturn.append("<td valign=top>" + seconds + "</td>");
		sbReturn.append("</tr>");
		startTime = endTime;
		iLastSqlCount = this.ebdy.getSqlCount();
		return sbReturn.toString();
	}

	private Calendar getLatestStart(int iI, Calendar dtStart) {
		Calendar dtReturn = null;
		if (dtStart != null)
			dtReturn = (Calendar) dtStart.clone();
		else {
			dtReturn = Calendar.getInstance();
			dtReturn.add(Calendar.DAY_OF_YEAR, +1);
		}

		Calendar dtEnd2 = null;

		if (aCp[iI] != null) {
			// pre-fixed date
			if (aCp[iI].dtStart != null && aCp[iI].dtStart.after(dtReturn)) {
				dtReturn = (Calendar) aCp[iI].dtStart.clone();
			}
			for (int iD = 0; iD < iMaxRecId; iD++) {
				if (aCp[iI].aDependency[iD] > 0) {
					if (iI > 0
							|| (aCp[aCp[iI].aDependency[iD]].iFLags & 0x8000) != 0) {
						dtEnd2 = (Calendar) aCp[aCp[iI].aDependency[iD]].dtEnd
								.clone();
						double dDays = 0; // aCp[aCp[iI].aDependency[iD]].dLag;
						dDays = aCp[iI].aDependencyLag[iD];
						if (dDays > 0) {
							if (dDays > aCp[iI].dLag)
								aCp[iI].dLag = dDays;
							for (double d = 0; d < dDays; d++) {
								dtEnd2.add(Calendar.DAY_OF_YEAR, +1);
								int iLoop = 0;
								do {
									iLoop = aCp[aCp[iI].aDependency[iD]]
											.checkHoliday(dtEnd2);
									if (iLoop > 0)
										dtEnd2.add(Calendar.DAY_OF_YEAR, iLoop);
								} while (iLoop != 0);
							}
						} else if (dDays < 0) // Negative LAG, start earlier
						{
							if (dDays < aCp[iI].dLag)
								aCp[iI].dLag = dDays;
							for (double d = 0; d > dDays; d--) {
								dtEnd2.add(Calendar.DAY_OF_YEAR, -1);
								int iLoop = 0;
								int iTemp = 0;
								do {
									iLoop = aCp[aCp[iI].aDependency[iD]]
											.checkHoliday(dtEnd2);
									if (iLoop > 0)
										dtEnd2.add(Calendar.DAY_OF_YEAR, -iLoop);
								} while (iLoop != 0 && iTemp++ < 1000);
							}
						}
					} else {
						stError += "<BR>ERROR getLatestStart: not all children are set "
								+ aCp[iI].aDependency[iD];
						dtEnd2 = null; // dont know when it is
					}

					// if (dtEnd2 != null) {
					if (dtEnd2 != null
							&& (aCp[iI].dLag < 0 || dtEnd2.after(dtReturn))) {
						dtReturn = (Calendar) dtEnd2.clone();
					}
				}
			}
		}
		return dtReturn;
	}

	Calendar calcStartEnd(int iI, Calendar dtStart, int iPrev) {
		Calendar dtReturn = null;
		Calendar dtEnd2 = null;
		int iLoop = 0;
		int[] aChain = new int[iMaxRecId];
		int iChain = 0;
		double dPreviousHours = 0;
		try {
			if (aCp[iI] != null && (aCp[iI].iFLags & 0x8000) != 0)
				return aCp[iI].dtEnd; // ------------------------------------>

			if (aCp[iI] == null) {
				stError += "<BR>ERROR calcStartEnd aCp is null for i = " + iI;
				return dtStart; // ------------------------------------------->
			}

			// Walk through all dependends
			for (int iD = 0; iD < iMaxRecId; iD++) {
				if (aCp[iI].aDependency[iD] > 0) {
					// Check if Dependent already processed or not
					// Need to handle Type of dependency
					iChain = 0;
					if ((aCp[aCp[iI].aDependency[iD]].iFLags & 0x8000) == 0) {
						// Need to back Track to first Finished task and forward
						// track to
						// here
						int iCp = aCp[iI].aDependency[iD];
						// Get Front of Path for start Date
						aChain[iChain] = iCp;
						iChain++;
						while (iCp > 0 && aCp[iCp] != null
								&& (aCp[iCp].iFLags & 0x8000) == 0) {
							aChain[iChain] = aCp[iCp].aDependency[0];
							iCp = aChain[iChain];
							iChain++;
							if (iLoop++ > iMaxRecId) {
								this.stError += "<BR>ERROR Loop in BackTrack "
										+ iCp;
								break;
							}
						}
						if (iChain > 1) {
							iChain--;
							for (; iChain > 0; iChain--) {
								if (aChain[iChain] == 0
										|| (aCp[aChain[iChain]] != null && (aCp[aChain[iChain]].iFLags & 0x100) != 0))
									dtEnd2 = this.dtProjectStart;
								else {
									dtEnd2 = aCp[aChain[iChain]].dtEnd;
								}
								if (aCp[aChain[iChain]] != null)
									dPreviousHours = aCp[aChain[iChain]].dHoursEnd;
								else
									dPreviousHours = 0;
								if (aCp[aChain[iChain - 1]] != null) {
									if (aCp[iI].aDependency[iD] == aChain[iChain - 1]) {
										if (aCp[iI].aDependencyType[iD]
												.equals("ss")) { // Start
																	// to
																	// Start
											calcStartEnd(aChain[iChain - 1],
													dtStart, aChain[iChain]); // Set
																				// dependent
																				// to
																				// same
																				// start
																				// as
																				// us
										} else if (aCp[iI].aDependencyType[iD]
												.equals("ff")) { // Finish
																	// to
																	// Finish
											dtStart = getLatestStart(iI,
													dtStart);
											aCp[iI].dtStart = dtStart;
											aCp[iI].calculateEnd(dPreviousHours);
											aCp[aChain[iChain]].dtEnd = (Calendar) aCp[iI].dtEnd
													.clone(); // Set
																// My
																// end
																// to
																// depend
																// end
											aCp[aChain[iChain]]
													.calculateStart(); // calculate
																		// depend
																		// start
										} else if (aCp[iI].aDependencyType[iD]
												.equals("sf")) { // Start
																	// to
																	// Finish
																	// (is
																	// reverse
																	// of
																	// Finish
																	// to
																	// Start
																	// dtEnd2
																	// =
																	// calcStartEnd(aChain[iChain
																	// -
																	// 1],
																	// dtStart);
																	// //
																	// Start
																	// dependent
																	// on
																	// my
																	// START
																	// dtStart
																	// =
																	// (Calendar)
																	// dtEnd2.clone();
																	// //
																	// set
																	// my
																	// Start
																	// to
																	// prev.
																	// end
											dtEnd2 = calcStartEnd(
													aChain[iChain - 1], dtEnd2,
													aChain[iChain]); // we
																		// treat
																		// SF
																		// like
																		// FS
																		// since
																		// To/from
																		// reversed
										} else { // Normal Finish to Start
											dtEnd2 = calcStartEnd(
													aChain[iChain - 1], dtEnd2,
													aChain[iChain]);
										}
									} else {
										dtEnd2 = calcStartEnd(
												aChain[iChain - 1], dtEnd2,
												aChain[iChain]); // Not
																	// our
																	// dependent
									}
								}
								if (iLoop++ > iMaxRecId) {
									this.stError += "<BR>ERROR Loop2 in BackTrack "
											+ iCp;
									break;
								}
							}
						}
					}
				} else
					break;
			}
			if ((aCp[iI].iFLags & 0x8000) == 0) {
				int iD = 0; // Don't know how to handle multple deps' here
				if (aCp[iI].aDependency[iD] > 0
						&& (aCp[aCp[iI].aDependency[iD]].iFLags & 0x8000) != 0) // Special
																				// case
																				// of
																				// dep.
				{
					if (aCp[iI].aDependencyType[iD].equals("ss")) { // Start to
																	// Start
						dtStart = (Calendar) aCp[aCp[iI].aDependency[iD]].dtStart
								.clone();
					} else if (aCp[iI].aDependencyType[iD].equals("ff")) { // Finish
																			// to
																			// Finish
						aCp[iI].dtEnd = (Calendar) aCp[aCp[iI].aDependency[iD]].dtEnd
								.clone();
						aCp[iI].calculateStart(); // calculate depend start
						dtStart = aCp[iI].dtStart;
					} else if (aCp[iI].aDependencyType[iD].equals("sf")) {
						dtStart = getLatestStart(iI, dtStart); // we treat SF
																// like FS since
																// To/From
																// reversed
					} else // Normal Finish to Start
					{
						dtStart = getLatestStart(iI, dtStart);
					}
				} else {
					dtStart = getLatestStart(iI, dtStart);
				}
				if (aCp[iPrev] != null
						&& !aCp[iI].aDependencyType[iD].equals("ss"))
					dPreviousHours = aCp[iPrev].dHoursEnd;
				else
					dPreviousHours = 0;
				aCp[iI].setStartCalculateEnd(dtStart, dPreviousHours);
				aCp[iI].iFLags |= 0x8000;
			}
			dtReturn = aCp[iI].dtEnd;
		} catch (Exception e) {
			this.stError += "<BR>ERROR calcStartEnd i: " + iI + " " + e;
		}
		return dtReturn;
	}

	public String findPaths(int iFrom) {
		StringBuilder sbReturn = new StringBuilder(5000);
		try {
			for (int iP = iFrom; iP < aCp.length; iP++) {
				if (aCp[iP] != null && aCp[iP].iFLags == 1) // Root and NOT
															// followed yet
				{
					this.aaPath[iMaxPath] = new int[iMaxRecId];
					for (int i2 = 0; i2 < iMaxRecId; i2++)
						aaPath[iMaxPath][i2] = 0;
					this.iPathPosition = 0;
					followPath(1, iP);
					iMaxPath++;
				}
			}
			// finish the rest.
			for (int iP = 0; iP < aCp.length; iP++) {
				if (aCp[iP] != null && (aCp[iP].iFLags & 0x100) == 0) // Non
																		// processed
																		// ID's
				{
					this.aaPath[iMaxPath] = new int[iMaxRecId];
					for (int i2 = 0; i2 < iMaxRecId; i2++)
						aaPath[iMaxPath][i2] = 0;
					this.iPathPosition = 0;
					followHead(iP);
					followPath(2, iP);
					iMaxPath++;
				}
			}
		} catch (Exception e) {
			this.stError += "<br>ERROR findPaths " + e;
		}
		return sbReturn.toString();
	}

	public String followHead(int iP) {
		StringBuilder sbReturn = new StringBuilder(5000);
		try {
			int[] aList = new int[iMaxRecId];
			int iMax = 0;
			for (int i = 0; i < aList.length; i++)
				aList[i] = 0;

			while (iP > 0 && aCp[iP] != null && aCp[iP].aDependency[0] != 0) {
				aList[iMax++] = aCp[iP].aDependency[0];
				iP = aCp[iP].aDependency[0];
			}
			for (int i = iMax - 1; i >= 0; i--) {
				iP = aList[i];
				aaPath[iMaxPath][this.iPathPosition] = iP;
				iPathPosition++;
				aCp[iP].iFLags |= 0x100;
			}
		} catch (Exception e) {
			this.stError += "<br>ERROR followHead " + e;
		}
		return sbReturn.toString();
	}

	public String followPath(int iType, int iP) {
		StringBuilder sbReturn = new StringBuilder(5000);
		int iFollow = 0;
		try {
			if (aCp[iP] != null) {
				aaPath[iMaxPath][this.iPathPosition] = iP;
				iPathPosition++;
				aCp[iP].iFLags |= 0x100;

				for (int iC = 0; iC < 20; iC++) {
					if (aCp[iP].aSuccessor[iC] != 0) {
						if (aCp[iP].aSuccessorProcess[iC] == 0) {
							followPath(iType, aCp[iP].aSuccessor[iC]);
							aCp[iP].aSuccessorProcess[iC]++; // Mark processed
							iFollow++;
							break;
						}
					} else
						break;
				}
				if (iFollow == 0 && iType == 2) {
					for (int iC = 0; iC < 20; iC++) {
						if (aCp[iP].aSuccessor[iC] != 0) {
							followPath(iType, aCp[iP].aSuccessor[iC]);
							aCp[iP].aSuccessorProcess[iC]++; // Mark processed
							iFollow++;
							break;
						} else
							break;
					}
				}

			}
		} catch (Exception e) {
			this.stError += "<br>ERROR followPath " + e;
		}
		return sbReturn.toString();
	}

	public void setDependendies(int iR) {
		if (aCp[iR] != null) {
			for (int i = 0; i < aCp[iR].aSuccessor.length; i++) {
				if (aCp[iR].aSuccessor[i] > 0
						&& aCp[aCp[iR].aSuccessor[i]] != null) {
					aCp[aCp[iR].aSuccessor[i]]
							.addDependency(iR, aCp[iR].aSuccessorType[i],
									aCp[iR].aSuccessorLag[i]);
				} else
					break;
			}
		}
	}

	public void calcUpdateBaseline() {
		try {
			String stSql = "update teb_baseline b set"
					+ " nmReqt=(SELECT count(*) FROM Requirements where nmProjectId=b.nmProjectId and nmBaseline=b.nmBaseline),"
					+ " nmSch=(SELECT count(*) FROM Schedule where nmProjectId=b.nmProjectId and nmBaseline=b.nmBaseline),"
					+ " nmWBS=(SELECT count(*) FROM WBS where nmProjectId=b.nmProjectId and nmBaseline=b.nmBaseline),"
					+ " nmEffort=(SELECT sum(SchEstimatedEffort) FROM Schedule where (SchFlags & 0x10 ) != 0 and nmProjectId=b.nmProjectId and nmBaseline=b.nmBaseline),"
					+ " nmCost=(SELECT sum(SchCost) FROM Schedule where (SchFlags & 0x10 ) != 0 and nmProjectId=b.nmProjectId and nmBaseline=b.nmBaseline)"
					+ " where b.nmProjectId=" + stPk + " and b.nmBaseline="
					+ nmBaseline;
			this.ebdy.ExecuteUpdate(stSql);
		} catch (Exception e) {
			this.stError += "<br>ERROR calcUpdateBaseline " + e;
		}
	}

	/*
	 * Calculate requirement cost for all requirements linked to this schedule
	 */
	public void processScheduleRequirementCost(int scheduleID)
			throws SQLException {
		// get all requirements that are linked to this schedule
		ResultSet rsReqs = this.ebdy.ExecuteSql("select * from teb_link"
				+ " where nmLinkFlags=1 and nmToProject=" + stPk
				+ " and nmToBaseline=" + this.nmBaseline + " and nmToId="
				+ scheduleID);
		rsReqs.last();
		for (int i = 1, iMax = rsReqs.getRow(); i <= iMax; i++) {
			int iPrjPk = rsReqs.getInt("nmFromProject");
			int iPrjBaseline = rsReqs.getInt("nmFromBaseline");
			int iReqRecId = rsReqs.getInt("nmFromId");
			EpsXlsProject epsPrj = new EpsXlsProject();
			epsPrj.setEpsXlsProject(this.epsUd);
			epsPrj.nmBaseline = iPrjBaseline;
			epsPrj.stPk = "" + iPrjPk;
			epsPrj.processRequirementCost(iReqRecId);
		}
	}

	/*
	 * Calculate requirement costs for the current project
	 */
	public void processRequirementCost(int reqID) throws SQLException {
		// get requirements we need to calculate
		Double rCost = 0.00;
		DecimalFormat df = new DecimalFormat("#########0.00");

		ResultSet rsP = this.ebdy
				.ExecuteSql("select count(*) cnt, sum(ReqCost) cost from requirements where nmProjectId="
						+ stPk
						+ " and nmBaseline="
						+ this.nmBaseline
						+ " and ReqParentRecId=" + reqID);
		boolean lowlvl = true;
		if (rsP.next()) {
			if (rsP.getInt("cnt") > 0) {
				lowlvl = false;
				rCost = rsP.getDouble("cost");
			}
		}

		if (lowlvl) {
			// calculate sum of tasks for each requirement cost
			ResultSet rResult = this.ebdy
					.ExecuteSql("select l.* from teb_link l"
							+ " left join requirements r on l.nmFromId=r.RecId and l.nmFromProject=r.nmProjectId and l.nmFromBaseline=r.nmBaseline"
							+ " where l.nmLinkFlags=1 and r.nmProjectId="
							+ stPk + " and r.nmBaseline=" + this.nmBaseline
							+ " and r.RecId=" + reqID);

			while (rResult.next()) {
				String stCost = this.ebdy
						.ExecuteSql1("select SchCost from schedule where nmProjectId="
								+ rResult.getString("l.nmToProject")
								+ " and nmBaseline="
								+ rResult.getString("l.nmToBaseline")
								+ " and RecId=" + rResult.getString("l.nmToId"));
				double dSchCost = 0;
				try {
					dSchCost = Double.parseDouble(stCost);
				} catch (Exception e) {
					dSchCost = 0;
				}

				if (rResult.getString("l.nmRemainder").equals("1"))
					rCost += dSchCost;
				else
					rCost += dSchCost
							* (rResult.getDouble("l.nmPercent") * 0.01);
			}
		}
		this.ebdy.ExecuteUpdate("update requirements set ReqCost='"
				+ df.format(rCost) + "' where nmProjectId=" + stPk
				+ " and RecId=" + reqID + " and nmBaseline=" + this.nmBaseline);
		int nmParentId = this.ebdy
				.ExecuteSql1n("select ReqParentRecId from requirements where nmProjectId="
						+ stPk
						+ " and nmBaseline="
						+ this.nmBaseline
						+ " and RecId=" + reqID);
		if (nmParentId > 0)
			processRequirementCost(nmParentId);
	}

	public void processAllRequirementCost() {
		try {
			ResultSet stResult = this.ebdy
					.ExecuteSql("select nmProjectId, nmBaseline, RecId, ReqFlags from requirements where nmProjectId="
							+ stPk + " order by ReqLevel desc");
			while (stResult.next()) {
				String reqID = stResult.getString("RecId");
				String projID = stResult.getString("nmProjectId");
				String bsline = stResult.getString("nmBaseline");
				boolean lowlvl = (stResult.getInt("ReqFlags") & 0x10) != 0;
				Double rCost = 0.00;
				String stCost = "";
				DecimalFormat df = new DecimalFormat("#########0.00");

				// calculate sum of tasks for each requirement cost
				if (lowlvl) {
					ResultSet rResult = this.ebdy
							.ExecuteSql("select l.* from teb_link l"
									+ " left join requirements r on l.nmFromId=r.RecId and l.nmFromProject=r.nmProjectId and l.nmFromBaseline=r.nmBaseline"
									+ " where l.nmLinkFlags=1 and r.nmProjectId="
									+ projID + " and r.nmBaseline=" + bsline
									+ " and r.RecId=" + reqID);

					while (rResult.next()) {
						stCost = this.ebdy
								.ExecuteSql1("select SchCost from schedule where nmProjectId="
										+ rResult.getString("l.nmToProject")
										+ " and nmBaseline="
										+ rResult.getString("l.nmToBaseline")
										+ " and RecId="
										+ rResult.getString("l.nmToId"));
						if (stCost == null || stCost.equals(""))
							stCost = "0";
						if (rResult.getString("l.nmRemainder").equals("1"))
							rCost += Double.parseDouble(stCost);
						else
							rCost += Double.parseDouble(stCost)
									* (Double.parseDouble(rResult
											.getString("l.nmPercent")) * 0.01);
					}
					this.ebdy
							.ExecuteUpdate("update requirements set ReqCost='"
									+ df.format(rCost) + "' where nmProjectId="
									+ projID + " and RecId=" + reqID
									+ " and nmBaseline=" + bsline);
				} else {
					stCost = this.ebdy
							.ExecuteSql1("select sum(ReqCost) from requirements where nmProjectId="
									+ projID
									+ " and ReqParentRecId="
									+ reqID
									+ " and nmBaseline=" + bsline);
					rCost = Double.parseDouble(stCost == null
							|| "".equals(stCost) ? "0" : stCost);
					this.ebdy
							.ExecuteUpdate("update requirements set ReqCost='"
									+ df.format(rCost) + "' where nmProjectId="
									+ projID + " and RecId=" + reqID
									+ " and nmBaseline=" + bsline);
				}
			}
		} catch (Exception e) {
			stError += "ERROR processAllRequirementCost: " + e;
		}
	}

	/*
	 * Flag low level tasks #110
	 */

	public int setEntryParent(String stChild, String stPrjId, int nmBaseline,
			String stRecId) {
		String stSql = "";
		int iParentRecId = 0;
		try {
			if ("19".equals(stChild)) {
				// calculate parent level and add update parent id if not high
				// level
				int rId = 0;
				int rLvl = this.ebdy
						.ExecuteSql1n("SELECT ReqLevel FROM requirements WHERE RecId = "
								+ stRecId
								+ " AND nmProjectId="
								+ stPrjId
								+ " AND nmBaseLine=" + nmBaseline);
				if (rLvl > 0) {
					// edit parent according to level
					rId = this.ebdy
							.ExecuteSql1n("SELECT ReqId FROM requirements WHERE RecId = "
									+ stRecId
									+ " AND nmProjectId="
									+ stPrjId
									+ " AND nmBaseLine=" + nmBaseline);
					ResultSet rsParent = this.ebdy
							.ExecuteSql("SELECT RecId,ReqLevel FROM requirements WHERE ReqId < "
									+ rId
									+ " AND ReqLevel<"
									+ rLvl
									+ " AND nmProjectId="
									+ stPrjId
									+ " AND nmBaseLine="
									+ nmBaseline
									+ " ORDER BY ReqId DESC LIMIT 0,1");
					if (rsParent.next()) {
						iParentRecId = rsParent.getInt("RecId");
						rLvl = rsParent.getInt("ReqLevel") + 1;
						this.ebdy
								.ExecuteUpdate("UPDATE requirements SET ReqFlags=((ReqFlags&~0x10)|0x4)"
										+ " WHERE nmProjectID="
										+ stPrjId
										+ " AND nmBaseline="
										+ nmBaseline
										+ " AND RecId=" + iParentRecId);
					}
				}
				stSql = "UPDATE requirements SET ReqParentRecId="
						+ iParentRecId + ",ReqLevel=" + rLvl
						+ " WHERE nmProjectID=" + stPrjId + " AND nmBaseline="
						+ nmBaseline + " AND RecId=" + stRecId;
			}
			if ("21".equals(stChild)) {
				// calculate parent level and add update parent id if not high
				// level
				int rId = 0;
				int rLvl = this.ebdy
						.ExecuteSql1n("SELECT SchLevel FROM schedule WHERE RecId = "
								+ stRecId
								+ " AND nmProjectId="
								+ stPrjId
								+ " AND nmBaseLine=" + nmBaseline);
				if (rLvl > 0) {
					// edit parent according to level
					rId = this.ebdy
							.ExecuteSql1n("SELECT SchId FROM schedule WHERE RecId = "
									+ stRecId
									+ " AND nmProjectId="
									+ stPrjId
									+ " AND nmBaseLine=" + nmBaseline);
					ResultSet rsParent = this.ebdy
							.ExecuteSql("SELECT RecId,SchLevel FROM schedule WHERE SchId < "
									+ rId
									+ " AND SchLevel<"
									+ rLvl
									+ " AND nmProjectId="
									+ stPrjId
									+ " AND nmBaseLine="
									+ nmBaseline
									+ " ORDER BY SchId DESC LIMIT 0,1");
					if (rsParent.next()) {
						iParentRecId = rsParent.getInt("RecId");
						rLvl = rsParent.getInt("SchLevel") + 1;
						this.ebdy
								.ExecuteUpdate("UPDATE schedule SET SchFlags=((SchFlags&~0x10)|0x4)"
										+ " WHERE nmProjectID="
										+ stPrjId
										+ " AND nmBaseline="
										+ nmBaseline
										+ " AND RecId=" + iParentRecId);
					}
				}
				stSql = "UPDATE schedule SET SchParentRecId=" + iParentRecId
						+ ",SchLevel=" + rLvl + " WHERE nmProjectID=" + stPrjId
						+ " AND nmBaseline=" + nmBaseline + " AND RecId="
						+ stRecId;
			}
			if ("90".equals(stChild)) {
				// calculate parent level and add update parent id if not high
				// level
				int rId = 0;
				int rLvl = this.ebdy
						.ExecuteSql1n("SELECT WBSLevel FROM WBS WHERE RecId = "
								+ stRecId + " AND nmProjectId=" + stPrjId
								+ " AND nmBaseLine=" + nmBaseline);
				if (rLvl > 0) {
					// edit parent according to level
					rId = this.ebdy
							.ExecuteSql1n("SELECT WBSSeqId FROM WBS WHERE RecId = "
									+ stRecId
									+ " AND nmProjectId="
									+ stPrjId
									+ " AND nmBaseLine=" + nmBaseline);
					ResultSet rsParent = this.ebdy
							.ExecuteSql("SELECT RecId,WBSLevel FROM WBS WHERE WBSSeqId < "
									+ rId
									+ " AND WBSLevel<"
									+ rLvl
									+ " AND nmProjectId="
									+ stPrjId
									+ " AND nmBaseLine="
									+ nmBaseline
									+ " ORDER BY WBSSeqId DESC LIMIT 0,1");
					if (rsParent.next()) {
						iParentRecId = rsParent.getInt("RecId");
						rLvl = rsParent.getInt("WBSLevel") + 1;
						this.ebdy
								.ExecuteUpdate("UPDATE WBS SET WBSFlags=((WBSFlags&~0x10)|0x4)"
										+ " WHERE nmProjectID="
										+ stPrjId
										+ " AND nmBaseline="
										+ nmBaseline
										+ " AND RecId=" + iParentRecId);
					}
				}
				stSql = "UPDATE WBS SET WBSParentRecId=" + iParentRecId
						+ ",WBSLevel=" + rLvl + " WHERE nmProjectID=" + stPrjId
						+ " AND nmBaseline=" + nmBaseline + " AND RecId="
						+ stRecId;
			}
			this.ebdy.ExecuteUpdate(stSql);
		} catch (SQLException e) {
			stError += "ERROR setEntryParent: " + e;
		}
		return iParentRecId;
	}

	public void recalcSchedule(String stPk, String stProject, int nmBaseline) {
		try {
			String stAllocateWhere = " WHERE nmPrjId=" + stProject
					+ " AND nmBaseline=" + nmBaseline + " AND nmTaskID=" + stPk;
			String stWhere = " WHERE nmProjectId=" + stProject
					+ " AND nmBaseline=" + nmBaseline + " AND RecId=" + stPk;
			ResultSet rs = this.ebdy.ExecuteSql("select * from Schedule"
					+ stWhere);
			rs.absolute(1);

			int iSchFlags = rs.getInt("SchFlags");
			if ((iSchFlags & 0x10) != 0) {
				String[] aRecords = null;
				String[] aFields = null;
				String stValue;
				double dExpended = 0;
				double dEstimated = 0;
				double dLCAvg = 0;
				double dEstimatedEffort = 0;

				String stSchStatus = rs.getString("SchStatus");
				// 39~1~160.0~~~~~|10~1~10~~~~~
				stValue = rs.getString("SchLaborCategories");
				if (stValue != null && stValue.length() > 0) {
					aRecords = stValue.split("\\|", -1);
					for (int iR = 0; iR < aRecords.length; iR++) {
						aFields = aRecords[iR].split("~", -1);
						dEstimatedEffort += Double.parseDouble(aFields[2]);
						double dCost = this.ebdy
								.ExecuteSql1nm("SELECT IFNULL(SUM(u.HourlyRate*ta.nmActualApproved),0)"
										+ " FROM teb_allocateprj ta, Users u"
										+ " WHERE ta.nmUserId=u.nmUserId and ta.nmPrjId="
										+ rs.getInt("nmProjectId")
										+ " AND ta.nmBaseline="
										+ rs.getInt("nmBaseline")
										+ " AND ta.nmTaskId="
										+ rs.getInt("RecId")
										+ " AND ta.nmLc="
										+ aFields[0]
										+ " GROUP BY ta.nmPrjId, ta.nmBaseline, ta.nmTaskId");

						double dHours = Double.parseDouble(aFields[2])
								- this.ebdy
										.ExecuteSql1nm("SELECT IFNULL(SUM(ta.nmActualApproved),0)"
												+ " FROM teb_allocateprj ta, Users u"
												+ " WHERE ta.nmUserId=u.nmUserId and ta.nmPrjId="
												+ rs.getInt("nmProjectId")
												+ " AND ta.nmBaseline="
												+ rs.getInt("nmBaseline")
												+ " AND ta.nmTaskId="
												+ rs.getInt("RecId")
												+ " AND ta.nmLc="
												+ aFields[0]
												+ "  GROUP BY ta.nmPrjId, ta.nmBaseline, ta.nmTaskId");
						double dAvg = this.ebdy
								.ExecuteSql1nm("select AverageHourlySalary from LaborCategory l where l.nmLcId="
										+ aFields[0]);
						dLCAvg += dAvg;
						dExpended += dCost;
						dEstimated += dCost
								+ (dAvg * dHours > 0 ? dAvg * dHours : 0);
					}
					dLCAvg /= aRecords.length;
				}
				dEstimated += dLCAvg * rs.getDouble("SchRemainingHours");

				stValue = rs.getString("SchInventory");
				if (stValue != null && stValue.length() > 0) {
					aRecords = stValue.split("\\|");
					for (int iR = 0; iR < aRecords.length; iR++) { // 6~2~|3~1~
						aFields = aRecords[iR].split("~");
						int iQty = Integer.parseInt(aFields[1]);
						double dPrice = this.ebdy
								.ExecuteSql1n("select CostPerUnit from Inventory where RecId="
										+ aFields[0]);
						dEstimated += (iQty * dPrice);
						dExpended += (iQty * dPrice);
					}
				}
				stValue = rs.getString("SchOtherResources");
				if (stValue != null && stValue.length() > 0) {
					aRecords = stValue.split("\\|");
					for (int iR = 0; iR < aRecords.length; iR++) { // travel1~123.45~|t2~99.99~
						aFields = aRecords[iR].split("~");
						double dPrice = Double.parseDouble(aFields[1]);
						dEstimated += dPrice;
						dExpended += dPrice;
					}
				}

				if ("Done".endsWith(stSchStatus)) {
					iSchFlags &= ~0x1200;
					iSchFlags |= 0x1000;
				} else {
					iSchFlags &= ~0x1000;
				}

				this.ebdy
						.ExecuteUpdate("update Schedule set nmExpenditureToDate="
								+ dExpended
								+ ",SchCost="
								+ dEstimated
								+ ",SchRemainingCost="
								+ Math.max(dEstimated - dExpended, 0)
								+ ",SchEstimatedEffort="
								+ dEstimatedEffort
								+ ",SchEfforttoDate=(SELECT IFNULL(SUM(nmActual),0) FROM teb_allocateprj"
								+ stAllocateWhere
								+ "),SchPctDone="
								+ (stSchStatus.equals("Done") ? 100
										: (dEstimatedEffort == 0 ? 0
												: "LEAST(100*(SELECT IFNULL(SUM(nmActual),0)/"
														+ dEstimatedEffort
														+ " FROM teb_allocateprj"
														+ stAllocateWhere
														+ "),100)"))
								+ ", SchFlags=" + iSchFlags + stWhere);
			} else {
				String stParentWhere = " WHERE nmProjectId=" + stProject
						+ " AND nmBaseline=" + nmBaseline
						+ " AND SchParentRecId=" + stPk;
				ResultSet rsParent = this.ebdy
						.ExecuteSql("SELECT SUM(SchEstimatedEffort) SchEstimatedEffort, SUM(SchEffortToDate) SchEffortToDate,"
								+ " SUM(SchCost) SchCost, SUM(nmExpenditureToDate) nmExpenditureToDate,"
								+ " MIN(SchStartDate) SchStartDate, MAX(SchFinishDate) SchFinishDate"
								+ " FROM Schedule" + stParentWhere);
				rsParent.absolute(1);
				double dParentEstimatedEffort = rsParent
						.getDouble("SchEstimatedEffort");
				double dParentEffortToDate = rsParent
						.getDouble("SchEffortToDate");
				double dParentEstimatedCost = rsParent.getDouble("SchCost");
				double dParentExpenditureCost = rsParent
						.getDouble("nmExpenditureToDate");
				String stParentStartDate = rsParent.getString("SchStartDate");
				String stParentFinishDate = rsParent.getString("SchFinishDate");
				int iStatus = this.ebdy
						.ExecuteSql1n("SELECT COUNT(*) FROM Schedule"
								+ stParentWhere + " AND SchStatus != 'Done'");
				String stStatus = "";
				if (iStatus == 0) {
					stStatus = "Done";
					iSchFlags &= ~0x1200;
					iSchFlags |= 0x1000;
				} else {
					iStatus = this.ebdy
							.ExecuteSql1n("SELECT COUNT(*) FROM Schedule"
									+ stParentWhere
									+ " AND SchStatus != 'Not Started'");
					stStatus = (iStatus == 0 ? "Not Started" : "In Progress");
					iSchFlags &= ~0x1000;
				}
				double dParentPercent = "Done".equals(stStatus) ? 100
						: (dParentEstimatedEffort == 0 ? 0 : Math.min(
								dParentEffortToDate * 100
										/ dParentEstimatedEffort, 100));
				this.ebdy
						.ExecuteUpdate("UPDATE Schedule SET"
								+ " SchLaborCategories='',SchInventory='',SchOtherResources=''"
								+ ",SchEstimatedEffort="
								+ dParentEstimatedEffort
								+ ", SchEffortToDate="
								+ dParentEffortToDate
								+ ", SchCost="
								+ dParentEstimatedCost
								+ ", nmExpenditureToDate="
								+ dParentExpenditureCost
								+ ", SchRemainingCost="
								+ Math.max(dParentEstimatedCost
										- dParentExpenditureCost, 0)
								+ ", SchPctDone="
								+ dParentPercent
								+ ",SchStatus="
								+ this.ebdy.fmtDbString(stStatus)
								+ ", SchFlags="
								+ iSchFlags
								+ ", SchStartDate="
								+ this.ebdy
										.fmtDbString(stParentStartDate)
								+ ", SchFinishDate="
								+ this.ebdy
										.fmtDbString(stParentFinishDate)
								+ stWhere);
				this.ebdy
						.ExecuteUpdate("delete from teb_link where nmLinkFlags=2"
								+ " AND ((nmToProject="
								+ stProject
								+ " and nmToBaseline="
								+ nmBaseline
								+ " and nmToId="
								+ stPk
								+ ") OR (nmFromProject="
								+ stProject
								+ " and nmFromBaseline="
								+ nmBaseline
								+ " and nmFromId="
								+ stPk
								+ ")) order by nmFromId");
			}
		} catch (Exception e) {
			stError += "<br>ERROR recalcSchedule [" + stPk + "] " + e;
		}
	}

	public void updateParentEfforts(String stPrjId, int nmBaseline,
			int iParentId, boolean isLoop) {
		int iParentRecId = iParentId;
		boolean looped = true;
		while (iParentRecId > 0 && looped) {
			recalcSchedule("" + iParentRecId, stPrjId, nmBaseline);
			iParentRecId = this.ebdy
					.ExecuteSql1n("SELECT SchParentRecId FROM Schedule WHERE nmProjectId="
							+ stPrjId
							+ " and nmBaseline="
							+ nmBaseline
							+ " and RecId=" + iParentRecId);
			looped = isLoop;
		}
	}

	public void updateProjectInfo() {
		this.ebdy
				.ExecuteUpdate("UPDATE projects"
						+ " SET ProjectEstimatedCost=IFNULL((SELECT SUM(SchCost) FROM schedule WHERE nmProjectId="
						+ this.stPk + " AND nmBaseline=" + this.nmBaseline
						+ " AND (SchFlags&0x10)!=0), 0)" + " WHERE RecId="
						+ this.stPk + " AND CurrentBaseline=" + this.nmBaseline);
		updateProjectIndex();
	}

	public void updateProjectIndex() {
		ResultSet rs = this.ebdy
				.ExecuteSql("select IFNULL(SUM(IF(s.SchStatus='Done',s.SchEstimatedEffort,0))/SUM(IF(s.SchStatus='Done',s.SchEfforttoDate,0)), 0) SPI,"
						+ "IFNULL(SUM(IF(s.SchStatus='Done',s.SchCost,0))/SUM(IF(s.SchStatus='Done',s.nmExpenditureToDate,0)), 0) CPI"
						+ " from Projects p"
						+ " join Schedule s on s.nmProjectId=p.RecId and s.nmBaseline=p.CurrentBaseline and s.lowlvl=1"
						+ " WHERE p.RecId="
						+ this.stPk
						+ " AND p.CurrentBaseline="
						+ this.nmBaseline
						+ " group by p.RecId");
		try {
			if (rs.next()) {
				double cpi = rs.getDouble("CPI");
				double spi = rs.getDouble("SPI");
				this.ebdy
						.ExecuteUpdate("REPLACE INTO trendline(`date`,`projectId`,`nmBaseline`,`cpi`,`spi`)"
								+ " VALUES(now(), "
								+ this.stPk
								+ ", "
								+ this.nmBaseline + "," + cpi + "," + spi + ")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/**
 * Translate Division Holidays to I18nJs holidays
 */
