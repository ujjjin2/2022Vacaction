package ch_1;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.DimensionUIResource;

import org.json.JSONObject;


public class ImageCollection extends JFrame implements ActionListener {
	
		
		private JLabel lblNorth;
		private JLabel labelsearch;
		private JPanel panelcenter;
		private JPanel panelsouth;
		private JTextField labeltf;
		private JLabel labelSL;
		private JTextField tfSL;
		private JButton buttonSL;
		private JButton btnSave;
		private JFileChooser jfc;
		private File dir;
		private String keyword;
		private String save;


		public ImageCollection(String title) {
			setTitle(title);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(350, 180);
			setLocation(600, 300);
			//setResizable(false);
			getContentPane().setLayout(new BorderLayout());
			
			
			lblNorth = new JLabel("KAKAO API를 이용한 사진모으기 프로젝트");
			lblNorth.setBorder(BorderFactory.createEmptyBorder(10 , 5, 10 , 5));
			lblNorth.setFont(new Font("굴림", Font.BOLD, 15));
			lblNorth.setHorizontalAlignment(JLabel.CENTER);
			add(lblNorth, BorderLayout.NORTH);

			panelcenter();
			panelsouth();

			setVisible(true);
		}
	
	
		private void panelsouth() {
			panelcenter = new JPanel();
			panelcenter.setLayout(null);
			panelcenter.setPreferredSize(new DimensionUIResource(100,45));
			
			labelsearch = new JLabel("키워드 검색");
			labelsearch.setBounds(13, 3, 100, 20);
			labelsearch.setFont(new Font("나눔", Font.BOLD, 14));
			panelcenter.add(labelsearch);
			
			labeltf = new JTextField(20);
			labeltf.setBounds(100, 4, 220, 20);
			labeltf.setFont(new Font("나눔", Font.BOLD, 13));
			panelcenter.add(labeltf);
			
			
			labelSL = new JLabel("저장경로");
			labelSL.setBounds(13, 40, 100, 20);
			labelSL.setFont(new Font("나눔", Font.BOLD, 13));
			panelcenter.add(labelSL);
			
			
			tfSL = new JTextField(10);
			tfSL.setBounds(100, 40, 150, 20);
			tfSL.setFont(new Font("나눔", Font.BOLD, 13));
			panelcenter.add(tfSL);
			
			buttonSL = new JButton("열기");
			buttonSL.setBounds(255, 40, 65, 18);
			buttonSL.addActionListener(this);
			buttonSL.setFont(new Font("나눔", Font.BOLD, 12));
			panelcenter.add(buttonSL);
			
			add(panelcenter,BorderLayout.CENTER);
			
		}
			
		private void panelcenter() {
			panelsouth = new JPanel();
			panelsouth.setLayout(null);
			panelsouth.setPreferredSize(new DimensionUIResource(100,30));
			
			btnSave = new JButton("저장");
			btnSave.setBounds(13, 0, 310, 22);
			btnSave.addActionListener(this);
			panelsouth.add(btnSave);
			
			add(panelsouth, BorderLayout.SOUTH);
		}


		 public static void main(String[] args) {
			 ImageCollection imgc = new ImageCollection("이미지 모으기 프로젝트");
		   }


		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			//파일 위치 열기
			if (obj == buttonSL) {
				jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.showDialog(this, null);
				dir = jfc.getSelectedFile();
				tfSL.setText(dir == null ? "" : dir.getPath());
			}
			//파일 저장하기
			else if (obj == btnSave) {
				if (labeltf.equals("")) {
					JOptionPane.showMessageDialog(this, "키워드를 입력해!");
				}else if (tfSL.equals("")) {
					JOptionPane.showMessageDialog(this, "저장경로를 선택해!");
				}else {
					keyword = labeltf.getText();
					save = tfSL.getText()+"\\";
					
					String restApiKey = "0d81a3134f5920b7381b8e515b973fe0";  // 개인 rest-api 키 입력

					try {
						String text = URLEncoder.encode("고양이", "UTF-8");
						String postParams = "src_lang=kr&target_lang=en&query=" + text;  // 파라미터
						String apiURL = "https://dapi.kakao.com/v2/search/image?" + postParams;
						URL url = new URL(apiURL);
						HttpURLConnection con = (HttpURLConnection)url.openConnection();
						String userCredentials = restApiKey;
						String basicAuth = "KakaoAK " + userCredentials;
						con.setRequestProperty("Authorization", basicAuth);
						
						// 이건 필요 유무 몰라서 빼놈
//						con.setRequestMethod("GET");
//						con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//						con.setRequestProperty("charset", "utf-8");
//						con.setUseCaches(false);
						//con.setDoInput(true);
						//con.setDoOutput(true);
						int responseCode = con.getResponseCode();
						System.out.println("responseCode >> " + responseCode);
						BufferedReader br;
						if(responseCode == 200) {
							br = new BufferedReader(new InputStreamReader(con.getInputStream()));
						}
						else {
							br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
						}
						String inputLine;
						StringBuffer res = new StringBuffer();
						while ((inputLine = br.readLine()) != null) {
							res.append(inputLine);
							
						}
						br.close();
						
						//System.out.println("응답결과>> " + res.toString());

						// 가장 큰 JSONObject를 가져옵니다.
					    JSONObject jObject = new JSONObject(res.toString());
					    // 배열을 가져옵니다.
					    org.json.JSONArray jArray = jObject.getJSONArray("documents");

					    String savePath = "C:\\test\\"; // 이미지 저장 파일
					    String fileFormat = "jpg";
					    

					    // 배열의 모든 아이템을 출력합니다.
					    for (int i = 0; i < jArray.length(); i++) {
					        JSONObject jobj = jArray.getJSONObject(i);
					        String imgURL = jobj.getString("image_url");
					        
					        String saveFileName = "test" + (i+1) + ".jpg";
					        
					        File saveFile = new File(savePath + saveFileName);
					        
					        saveImage(imgURL, saveFile, fileFormat);
					        
					        System.out.println("image_url(" + i + "): " + imgURL);

					        System.out.println();
					    }
					    } catch (Exception f) {
							f.printStackTrace();
							System.out.println("--확인용-- T_Test.java에서 오류 발생");
							System.out.println(f);
						}
						
					}
				}
			}
			public static void saveImage(String imageUrl, File saveFile, String fileFormat) {
		    
				URL url = null;
				BufferedImage bi = null;
			
				try {
					url = new URL(imageUrl); // 다운로드 할 이미지 URL
					bi = ImageIO.read(url);
					ImageIO.write(bi, fileFormat, saveFile); // 저장할 파일 형식, 저장할 파일명
				
				} catch (MalformedURLException e) {
				e.printStackTrace();
				} catch (IOException e) {
				e.printStackTrace();
				}
			
			}
}	
		
			
				


			    
			
		
		



	

