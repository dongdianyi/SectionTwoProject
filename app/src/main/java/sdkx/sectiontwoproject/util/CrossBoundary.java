package sdkx.sectiontwoproject.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import com.alibaba.fastjson.JSONArray;

import static sdkx.sectiontwoproject.util.UtilLog.showLogE;


/**
 * 判断车辆行驶过程是否出错的方法
 * @author wzr
 *4-22 版: 新增倒车超过若干厘米才判断为倒车
 */
public class CrossBoundary {
	public static double speedAngle = 0;
	/**是否出库了*/
	public static boolean isBack = false;
	
	/**倒车检测次数*/
	private static int backNumber = 0;
	/**在车库里到的车*/
	public static boolean inArea3 = false;

	/** 地球半径 **/
	private static final double R = 637139300;// 6378245 6371393
	/** 180° 保留10位小数 **/
	private static final DecimalFormat df = new DecimalFormat("0.0000000000");
	/** 提供了一组用于从“坐标”列表中构建“几何”对象的实用方法 */
	private static GeometryFactory gf = new GeometryFactory();
	/** 0: 合格 */
	public static final int QUALIFIED = 0;
	/** 1: 出界, 考试结束 */
	public static final int OUT_ERROR = 1;
	/** 2: 库外倒车, 考试结束 */
	public static final int ASTERNWAY = 2;
	/** 3: 碰杆, 考试结束 */
	public static final int PILE_ERROR = 3; 
	/** 4: 串口信息错误 */
	public static final int PORT_DATA_ERROR = 4; 
	
	private static double outLine = 0.0;//边界线内缩距离
	private static double pileLine = 3.0;//桩的半径
	private static double distance = 4.0;//倒车距离

	private List<Point> place = new ArrayList<Point>();// 场地的点集

	private List<Point> car = new ArrayList<Point>();// 车辆的点集

	private Point befrePoint = null;//开始倒车的第一个点
	private Point afterPoint = null;//开始倒车的最后一个点
	
//	private List<Point> route = new ArrayList<Point>();// 路线的点集 TODO ????
	/** 考生号 */
	private String examineeId;

	
	/**
	 * 运行前录入信息
	 * 
	 * @param placeStr   : 场地的字符串
	 * @param carStr     : 车辆的字符串
	 * @param examineeId : 考生id
	 * @param outLine : 边界
	 * @param pileLine : 桩
	 * @param distance : 倒车距离
	 *
	 */
	public void setMessage(String placeStr, String carStr, String examineeId, 
			double outLine, double pileLine, double distance) {
		this.place = JSONArray.parseArray(placeStr, Point.class);
		this.car = JSONArray.parseArray(carStr, Point.class);
		this.examineeId = examineeId;
		this.outLine = outLine;
		this.pileLine = pileLine;
		this.distance = distance;
		speedAngle = 0;
		backNumber = 0;
		isBack = false;
		inArea3 = false;
		befrePoint = null;
		afterPoint = null;
	}

	
	
	/**
	 * 处理串口传过来的数据
	 *
	 * @param data 串口传过来的数据
	 * @return ture: 越出边界; false:未越出边界
	 */
	public int isCross(String data) {
		// 如果有正在考试的考生
		try {// 接收完一整条数据
			Thread.sleep(5);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// 判断是否有考生考试?
//		if (this.examineeId != null && !"".equals(this.examineeId)) {
		if (!checkData(data)) {// 判断串口传的数据是否正确
			new Exception("串口传输数据错误").printStackTrace();
			return -1;
		}
		//返回解析后的数据
		Map<String, String> analysis = analysis(data);
		//  TODO 存到个list?
		Point gps = new Point(Double.valueOf(analysis.get("lng")), Double.valueOf(analysis.get("lat")));
		double angle = Double.valueOf(analysis.get("velocityAngle"));// 速度角

		int touch = touch(angle, gps, this.place, this.car);// ture: 越出边界; false:未越出边界
//		if (touch) {// 越出边界
			return touch;
//		} else {
//			return/ touch;
//		}
//		}
//		return false;
	}

	/**
	 * 数据解析 格式
	 * 
	 * @param data 开头$SNDH2 , 结尾*FF
	 * @return 合法数据返回一个Map<String, String> 其中 lng:经度 ; lat:纬度; vertex:角度;
	 *         gpsTime:gps发送时间 不合法数据返回null
	 */
	public Map<String, String> analysis(String data) {
		String[] split = data.split(",");
		Map<String, String> result = new HashMap<String, String>();
		result.put("lng", split[2]);
		result.put("lat", split[3]);
		result.put("velocityAngle", split[5]);
		result.put("sendTime", utcToLocal(split[1]));
		result.put("attr1", this.examineeId);// 考生的id
//        result.put("isfirst", MgrConst.isfirst);// 考生是否是补考
//		route.add( )
		return result;
	}

	/**
	 * 字符串转16进制
	 *
	 * @param data
	 * @return
	 */
	static String strToHex(String data) {
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			hex.append(Integer.toHexString(c));
		}
		return hex.toString();
	}

	/**
	 * 返回时间
	 *
	 * @return 字符串格式 yyyy-MM-dd HH:mm:ss eg. 2014-05-20 12:25:25
	 */
	public String utcToLocal(String date) {
		String[] split = date.split("\\.");
		String result = "";
		try {
			Date parse = new SimpleDateFormat("yyyyMMddHHmmss").parse(split[0]);
			// 市区转换, 加8小时
			result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(parse.getTime() + 8 * 60 * 60 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * hex转byte数组 , 解析数据用
	 *
	 * @param str : 16进制字符串, 无空格
	 * @return
	 */
	static String hexToHexString(String str) {
		byte[] bArray = str.getBytes();
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		String result = sb.toString();
		return result;
	}

	/**
	 * 校验数据的正确性
	 *
	 * @param
	 * @return true: 合格数据. false: 不合格数据
     *//*
	static boolean checkData(String data) {
		// 异或校验值
		String checkValue = data.substring(data.length() - 3, data.length()).replace("*", "");
		// 截出进行异或校验的数据内容, 并转成16进制
		String hex = strToHex(data.substring(1, data.length() - 3));
		long totle = 0;
		// 取到首位十六进制 0x**
		totle = Long.parseLong(hex.substring(0, 2), 16);
		long xor = 0;
		for (int i = 2; i < hex.length(); i += 2) {
			// 进行异或 0x** ^ 0x**
			xor = Long.parseLong(hex.substring(i, i + 2), 16);
			totle = totle ^ xor;
		}
		// 和发送的结果比较
		int parseInt = Integer.parseInt(checkValue, 16);
		return parseInt == totle;
	}
*/
    public boolean checkData(String data) {
        try {
            data = data.replaceAll("\r", "").replaceAll("\n", "");
            // 异或校验值
		String checkValue = data.substring(data.length() - 3, data.length()).replace("*", "");
		// 截出进行异或校验的数据内容, 并转成16进制
		String hex = strToHex(data.substring(1, data.length() - 3));
		long totle = 0;
		// 取到首位十六进制 0x**
		totle = Long.parseLong(hex.substring(0, 2), 16);
		long xor = 0;
		for (int i = 2; i < hex.length(); i += 2) {
			// 进行异或 0x** ^ 0x**
			xor = Long.parseLong(hex.substring(i, i + 2), 16);
			totle = totle ^ xor;
		}
		// 和发送的结果比较
		int parseInt = Integer.parseInt(checkValue, 16);
		return parseInt == totle;
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean checkDataStr(String data) {

        try {
            // 异或校验值
            if (data.length() <= 3) {
                return false;
            }
            String checkValue = data.substring(data.length() - 2, data.length());
            // 截出进行异或校验的数据内容, 并转成16进制
            String hex = data.substring(0, data.length() - 2);
            long totle = 0;
            // 取到首位十六进制 0x**
            totle = Long.parseLong(hex.substring(0, 2), 16);
            long xor = 0;
            for (int i = 2; i < hex.length(); i += 2) {
                // 进行异或 0x** ^ 0x**
                xor = Long.parseLong(hex.substring(i, i + 2), 16);
                totle = totle ^ xor;
            }
            // 和发送的结果比较
            String totleStr = Long.toHexString(totle);
            if (totleStr.length() == 1) {
                totleStr = "0" + totleStr;
            }
            return totleStr.equalsIgnoreCase(checkValue);
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** 根据一点的坐标与距离，以及方向，计算另外一点的位置
	 * 
	 * @param angle     角度，从正北顺时针方向开始计算
	 * @param startLong 起始点经度
	 * @param startLat  起始点纬度
	 * @param distance  距离，单位m
	 * @return
	 */
	public Point calLocationByDistanceAndLocationAndDirection(double angle, double startLong, double startLat,
			double distance) {
		// 将距离转换成经度的计算公式
		double δ = distance / R;
		// 转换为radian，否则结果会不正确
		angle = Math.toRadians(angle);
		startLong = Math.toRadians(startLong);
		startLat = Math.toRadians(startLat);
		double lat = Math.asin(Math.sin(startLat) * Math.cos(δ) + Math.cos(startLat) * Math.sin(δ) * Math.cos(angle));
		double lon = startLong + Math.atan2(Math.sin(angle) * Math.sin(δ) * Math.cos(startLat),
				Math.cos(δ) - Math.sin(startLat) * Math.sin(lat));
		// 转为正常的10进制经纬度
		lon = Math.toDegrees(lon);
		lat = Math.toDegrees(lat);
		return new Point(Double.valueOf(df.format(lon)), Double.valueOf(df.format(lat)));
	}

	/**
	 * 返回 转向后的车的边界点
	 * 
	 * @param angle 速度角
	 * @param gps   gps的经纬度
	 * @param list  车的边界点的参数
	 * @return 车的边界的点的经纬度的集合
	 */
	public List<Point> getVertex(double angle, Point gps, List<Point> list) {
		double α = 0.0;// gps点到某个点的方向角
		double distance = 0.0;// gps点到某个点的距离
		List<Point> result = new ArrayList<Point>();
		double vertex = 0.0;
		for (int i = 4; i < list.size(); i++) {
			Point newPoint = new Point();
			// 顶点到gps的距离
			distance = Math.abs(list.get(i).getHypotenuse());
			vertex = list.get(i).getVertex();
			// 原第一象限的点
			if (list.get(i).getHypotenuse() >= 0 && list.get(i).getSide() > 0) {
				α = angle - vertex;
				α = getα(α);
			} else
			// 原第二象限的点
			if (list.get(i).getHypotenuse() > 0 && list.get(i).getSide() <= 0) {
				α = angle + vertex;
				α = getα(α);
			} else
			// 原第三象限的点
			if (list.get(i).getHypotenuse() <= 0 && list.get(i).getSide() < 0) {
//				if(Double.isNaN(vertex)) {
//					vertex = 90;
//				}
				α = angle + 180 - vertex;
				α = getα(α);
			} else
			// 原第四象限的点
			if (list.get(i).getHypotenuse() < 0 && list.get(i).getSide() >= 0) {
				α = angle + 180 + vertex;
				α = getα(α);
			}
			newPoint = calLocationByDistanceAndLocationAndDirection(α, gps.getLng(), gps.getLat(), distance);
			result.add(newPoint);
		}

		return result;
	}

	/**
	 * 防止角度大于360度或小于0度
	 * 
	 * @param α : 方向角
	 * @return
	 */
	private double getα(double α) {
		if (α > 360) {
			α = α - 360;
		} else if (α < 0) {
			α += 360;
		}
		return α;

	}

	/**
	 * 根据点到直线的距离公式 求 直线外一点到直线的距离 (lng:x,lat:y)
	 * 
	 * @param pointA 直线上一点A
	 * @param pointB 直线上另一点B
	 * @param gps    直线外一点
	 * @return
	 */
	public double getDistance(Point pointA, Point pointB, Point gps) {
		double result = 0.0;
		// 分子
		result = (pointA.getLat() - pointB.getLat()) * gps.getLng()
				+ pointA.getLat() * (pointA.getLng() - pointB.getLng())
				- (pointA.getLat() - pointB.getLat()) * pointA.getLng()
				- (pointA.getLng() - pointB.getLng()) * gps.getLat();
		// 分母
		double denominator = Math.sqrt((pointA.getLat() - pointB.getLat()) * (pointA.getLat() - pointB.getLat())
				+ (pointA.getLng() - pointB.getLng()) * (pointA.getLng() - pointB.getLng()));
		// 结果
		result = Math.abs(result / denominator);
		return result;
	}

	/**
	 * 求角度 (改到录入数据库的时候计算)
	 * 
	 * @param side       直角三角形的对边
	 * @param Hypotenuse 直角三角形的斜边
	 */
	public static double getAngle(double side, double Hypotenuse) {
		return Math.toDegrees(Math.asin(Math.abs(side) / Math.abs(Hypotenuse)));
	}

	/**
	 * 把点集转变成多边形
	 * 
	 * @param points 经纬度点的集合
	 * @return 多边形
	 */
	public Geometry pointToGraph(List<Point> points) {
		if (points.size() > 0) {
			Coordinate[] coor = new Coordinate[points.size() + 1];//
			for (int i = 0; i < points.size(); i++) {
				coor[i] = new Coordinate(points.get(i).getLng(), points.get(i).getLat());
			}
			// 最后一个点是第一个点, 要求点集是一个首尾相连的闭环
			coor[coor.length - 1] = new Coordinate(points.get(0).getLng(), points.get(0).getLat());
			Geometry result = gf.createPolygon(coor);
			return result;
		} else {
			return null;
		}
	}

	/**
	 * 返回车辆是否越过边界
	 * 
	 * @param angle 速度角
	 * @param gps   gps的经纬度
	 * @param place 场地的边界的点的经纬度的集合
	 * @param car   车的边界点的参数
	 * @return ture: 越出边界; false:未越出边界
	 */
	public int touch(double angle, Point gps, List<Point> place, List<Point> car) {
		List<Point> turnCar = getVertex(angle, gps, car);// 得到车辆转向后的经纬度
		Geometry carGeo = pointToGraph(turnCar);// 形成车辆的图形
		Geometry placeGeo = pointToGraph(place);// 形成场地的图形(只生成一次?然后存储成static final?)
		Geometry hullCoords = hullCoords().difference(placeGeo);
		boolean isOut = carGeo.distance(hullCoords) * 1000000 <= outLine;// 是否出界
		if (isOut) {// 出界
			boolean isPile = pileTouches(carGeo, place, pileLine);// 警告触发线
			if (isPile) {// 撞杆
				showLogE("pileLine",pileLine+"");
				return PILE_ERROR;
			}
			return OUT_ERROR;
		}
			boolean inThreadArea = inThreadArea(gps, carGeo, place);
			if(!inThreadArea) {
				return ASTERNWAY;
			}
		return QUALIFIED;
	}

	
	/** 录入场地 */
	public void setPlace(String placeStr) {
		this.place = JSONArray.parseArray(placeStr, Point.class);
	}

	/** 录入车辆 */
	public void setCar(String carStr) {
		this.car = JSONArray.parseArray(carStr, Point.class);
	}

	/** 录入考试 */
	public void setExamineeId(String examineeId) {
		this.examineeId = examineeId;
	}

	/**
	 * 判断是否撞杆
	 * 
	 * @param carGeo   车
	 * @param place    场地
	 * @param distance 桩的圆的半径
	 * @return ture 有接触 \ false 无接触
	 */
	boolean pileTouches(Geometry carGeo, List<Point> place, double distance) {
		distance = 0.0000001 * distance;// 距离
		// 桩点
		Polygon pile2 = createCircle(place.get(2).getLng(), place.get(2).getLat(), distance);
		// ture 有接触 \ false 无接触
		double isPile = pile2.distance(carGeo);
		if (isPile == 0) {
			return true;
		}
		Polygon pile3 = createCircle(place.get(3).getLng(), place.get(3).getLat(), distance);
		isPile = pile3.distance(carGeo);
		if (isPile == 0) {
			return true;
		}
		Polygon pile4 = createCircle(place.get(4).getLng(), place.get(4).getLat(), distance);
		isPile = pile4.distance(carGeo);
		if (isPile == 0) {
			return true;
		}
		Polygon pile5 = createCircle(place.get(5).getLng(), place.get(5).getLat(), distance);
		isPile = pile5.distance(carGeo);
		if (isPile == 0) {
			return true;
		}
		return false;

	}

	/**
	 * 创建一个圆，圆心(x,y) 半径RADIUS
	 * 
	 * @return 近似圆的多边形
	 */
	public Polygon createCircle(double x, double y, final double RADIUS) {
		final int SIDES = 32;// 圆上面的点个数
		Coordinate coords[] = new Coordinate[SIDES + 1];
		for (int i = 0; i < SIDES; i++) {
			double angle = ((double) i / (double) SIDES) * Math.PI * 2.0;
			double dx = Math.cos(angle) * RADIUS;
			double dy = Math.sin(angle) * RADIUS;
			coords[i] = new Coordinate((double) x + dx, (double) y + dy);
		}
		coords[SIDES] = coords[0];
		LinearRing ring = gf.createLinearRing(coords);
		Polygon polygon = gf.createPolygon(ring, null);
		return polygon;
	}

	/**
	 * @return 中国范围的外壳
	 */
	Geometry hullCoords() {
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(88.33008000, 44.90258000));
		points.add(new Point(88.24219, 11.52309));
		points.add(new Point(137.10938, 11.69527));
		points.add(new Point(137.10938, 44.84029));
		Geometry hullCoords = pointToGraph(points);
		return hullCoords;
	}

	/**
	 * 返回车辆是否完全在车库里
	 * 
	 * @param carGeo 车的图形
	 * @param place 场地的边界的点的经纬度的集合
	 * @return ture: 越出边界; false:未越出边界
	 */
	public boolean inArea3(Geometry carGeo, List<Point> place) {

		List<Point> newPlace = new ArrayList<Point>();
		newPlace.add(place.get(2));
		newPlace.add(place.get(3));
		newPlace.add(place.get(4));
		newPlace.add(place.get(5));

		Geometry placeGeo = pointToGraph(newPlace);// 形成场地的图形(只生成一次?然后存储成static final?)

		boolean onTest = carGeo.within(placeGeo);// 测试车的图形是否完全在场地的图形中

		return onTest;
	}

	/**
	 * 	判断车辆是否倒车
	 * @param gps    gps的点
	 *  place3 场地的4点
	 *  place4 场地的5点
	 * @return true : 正常 ；false : 未在规定路线行驶（在库外倒车）
	 */
	public boolean inThreadArea(Point gps, Geometry carGeo, List<Point> place) {
		Point place3 = place.get(3);
		Point place4 = place.get(4);

		double a = distanceByLnglat(gps, place3);
		double b = distanceByLnglat(gps, place4);
		double c = distanceByLnglat(place3, place4);

		double cosC = (a * a + b * b - c * c) / (2 * a * b);
		double angle = Math.acos(cosC) * 180 / Math.PI;
		double badistance = 0.0;//倒车距离
		if(isBack) {//入库之后
			if (speedAngle >= angle) {// 一直在远离库底
				speedAngle = angle;
				backNumber = 0;//没有连续倒车归零
				befrePoint = null;
				afterPoint = null;
				return true;
			} else {// 第一次远离车库即倒车之后不可再次靠近库底
				backNumber++;
				if(backNumber == 1) {
					befrePoint = gps;
				}else {
					afterPoint = gps;
				}
				if(backNumber > 10) {//连续倒车点超过10次
					//计算累计10次的倒车距离
					badistance = distanceByLnglat(befrePoint, afterPoint) * 100;
					showLogE("距离",backNumber + " : isBackdistanceByLnglat: " + badistance);
					if(badistance >= distance) {
						return false ;
					}
					return true;
				}
				return true;
			}
		}
		
		if (speedAngle <= angle) {// 一直在靠近库底
			speedAngle = angle;
			backNumber = 0;//没有连续倒车归零
			befrePoint = null;
			afterPoint = null;
			return true;
		} else {// 第一次远离车库即倒车时判断是否在车库里
			backNumber++;
			if(backNumber == 1) {
				befrePoint = gps;
			}else {
				afterPoint = gps;
			}
			boolean onTest = inArea3(carGeo, place);// 测试车的图形是否完全在场地的图形中
			if(onTest || inArea3) {//在库里倒的车
				inArea3 = true;
				isBack = true;
				return true;
			}
			if(backNumber > 10) {//连续倒车点超过10次
				badistance = distanceByLnglat(befrePoint, afterPoint) * 100;
				showLogE("距离",backNumber + " : isBackdistanceByLnglat: " + badistance);
				if(badistance >= distance) {
					return false ;
				}
				return true;
			}
			return true;
		}
	}
	
	/** 算距离，没有四舍五入 单位:米 **/
	public double distanceByLnglat(Point a, Point b) {
		double lng1 = a.getLng();
		double lat1 = a.getLat();
		double lng2 = b.getLng();
		double lat2 = b.getLat();
		double latitude1 = (Math.PI / 180.0) * lat1;// 第一个点的纬度
		double latitude2 = (Math.PI / 180.0) * lat2;// 第二个点的纬度
		double longitude1 = (Math.PI / 180.0) * lng1;// 第一个点的经度
		double longitude2 = (Math.PI / 180.0) * lng2;// 第二个点的经度

		double temp = Math.sin(latitude1) * Math.sin(latitude2)
				+ Math.cos(latitude1) * Math.cos(latitude2) * Math.cos(longitude2 - longitude1);
		return Math.acos(temp) * 6378245;// 修正半径后的距离

	}
}

/**
 * 经纬度的点(lng, lat)
 * 
 * @author wzr
 *
 */
class Point {
	private String number;// 序号
	private double lng = 0;// 经度
	private double lat = 0;// 纬度
	private double side = 0;// 对边 y
	private double hypotenuse = 0;// 斜边 x
	private double vertex = 0;// 角度

	/**
	 * 判断所有属性是否有空的,""
	 * 
	 * @return
	 */
	public boolean isAllNull() {
		if (0 == this.lng) {
			System.out.println("lng is null");
			return false;
		}
		if (0 == this.lat) {
			System.out.println("lat is null");
			return false;
		}
		if (0 == this.side) {
			System.out.println("side is null");
			return false;
		}
		if (0 == this.hypotenuse) {
			System.out.println("hypotenuse is null");
			return false;
		}
		return true;
	}

	public Point() {
		super();
	}

	public Point(double lng, double lat) {
		super();
		this.lng = lng;
		this.lat = lat;
	}

	public Point(double side, double hypotenuse, double vertex) {
		super();
		this.side = side;
		this.hypotenuse = hypotenuse;
		this.vertex = vertex;
	}

	public Point(double lng, double lat, double side, double hypotenuse) {
		super();
		this.lng = lng;
		this.lat = lat;
		this.side = side;
		this.hypotenuse = hypotenuse;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public double getSide() {
		return side;
	}

	public void setSide(double side) {
		this.side = side;
	}

	public double getHypotenuse() {
		return hypotenuse;
	}

	public void setHypotenuse(double hypotenuse) {
		this.hypotenuse = hypotenuse;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getVertex() {
		return vertex;
	}

	public void setVertex(double vertex) {
		this.vertex = vertex;
	}
}
