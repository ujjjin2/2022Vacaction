package ch_3;

import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NaverMovieCrawling {
	//네이버 영화 리뷰에서 '헤어질 결심' 리뷰 제목 크롤링 하기
		public static void main(String[] args) {	
			String url = "https://movie.naver.com/movie/bi/mi/review.naver?code=198413";
			Document doc = null;
			
			try {
				doc = Jsoup.connect(url).get();
				Elements elements = doc.select("ul.rvw_list_area");
				Iterator<Element> lis = elements.select("strong").iterator();
				System.out.println("실행 되는중 ");
				
				int i =0;
				while(lis.hasNext()) {
					i++;
					System.out.println(i+". "+lis.next().text());
					
				}
				System.out.println(i + "개의 리뷰를 찾았습니다.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("실행안되는중 ");
				e.printStackTrace();
			}
		}
	}

