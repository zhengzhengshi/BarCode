using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.IO;
using System.Drawing;

using System.Drawing.Drawing2D;

using ZXing.Common;
using ZXing;
using ZXing.QrCode;


using NPOI.HSSF.UserModel;
using NPOI.SS.UserModel;
using System.Runtime.InteropServices;

namespace DeviceManager
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {

        public HSSFWorkbook workbook;
        ISheet sheet;

        public MainWindow()
        {
            InitializeComponent();
        }

        /// <summary>
        /// 生成一维条形码
        /// </summary>
        /// <param name="text">内容</param>
        /// <param name="width">宽度</param>
        /// <param name="height">高度</param>
        /// <returns></returns>
        public void GenerateBarcode(string text, int width, int height,int index)
        {
            BarcodeWriter writer = new BarcodeWriter();
            //使用ITF 格式，不能被现在常用的支付宝、微信扫出来
            //如果想生成可识别的可以使用 CODE_128 格式
            //writer.Format = BarcodeFormat.ITF;
            writer.Format = BarcodeFormat.CODE_128;

            EncodingOptions options = new EncodingOptions()
            {
                Width = width,
                Height = height,
                Margin = 0,
                PureBarcode = true
            };
            writer.Options = options;
            Bitmap bitmap_code = writer.Write(text);
            Bitmap bitmap = new Bitmap(width, height + 50);
  
            Graphics g = Graphics.FromImage(bitmap);
            g.SmoothingMode = SmoothingMode.AntiAlias;
            g.InterpolationMode = InterpolationMode.HighQualityBicubic;
            g.PixelOffsetMode = PixelOffsetMode.HighQuality;

            g.Clear(System.Drawing.Color.White);

            RectangleF rectf = new RectangleF(100, height, width, 50);
            g.DrawString(AddSpace(text, 1), new Font("Tahoma", 28), System.Drawing.Brushes.Black, rectf);
            
            MemoryStream ms = new MemoryStream();
            bitmap_code.Save(ms, System.Drawing.Imaging.ImageFormat.Bmp);
            System.Drawing.Image newImage = System.Drawing.Image.FromStream(ms);
            g.DrawImage(newImage, new PointF(0, 0));

            g.Flush();


            genExcel(bitmap,index);


            //IntPtr ip = bitmap.GetHbitmap();//从GDI+ Bitmap创建GDI位图对象
            //                                //Imaging.CreateBitmapSourceFromHBitmap方法，基于所提供的非托管位图和调色板信息的指针，返回一个托管的BitmapSource
            //BitmapSource bitmapSource = System.Windows.Interop.Imaging.CreateBitmapSourceFromHBitmap(ip, IntPtr.Zero, Int32Rect.Empty,
            //System.Windows.Media.Imaging.BitmapSizeOptions.FromEmptyOptions());
            //// DeleteObject(ip);
            //return bitmapSource;
        }

        private ImageSource createQRCode(String content, int width, int height)
        {
            EncodingOptions options;
            //包含一些编码、大小等的设置
            //BarcodeWriter :一个智能类来编码一些内容的条形码图像
            BarcodeWriter write = null;
            options = new QrCodeEncodingOptions
            {
                DisableECI = true,
                CharacterSet = "UTF-8",
                Width = width,
                Height = height,
                Margin = 10
            };
            write = new BarcodeWriter();
            //设置条形码格式
            write.Format = BarcodeFormat.QR_CODE;
            //获取或设置选项容器的编码和渲染过程。
            write.Options = options;
            //对指定的内容进行编码，并返回该条码的呈现实例。渲染属性渲染实例使用，必须设置方法调用之前。
            Bitmap bitmap = write.Write(content);
            IntPtr ip = bitmap.GetHbitmap();//从GDI+ Bitmap创建GDI位图对象
                                            //Imaging.CreateBitmapSourceFromHBitmap方法，基于所提供的非托管位图和调色板信息的指针，返回一个托管的BitmapSource
            BitmapSource bitmapSource = System.Windows.Interop.Imaging.CreateBitmapSourceFromHBitmap(ip, IntPtr.Zero, Int32Rect.Empty,
            System.Windows.Media.Imaging.BitmapSizeOptions.FromEmptyOptions());
           // DeleteObject(ip);
            return bitmapSource;
        }

        private void genExcel(Bitmap bitmap,int index)
        {
            int column_index = (index / 4)*3;
            int rowline  = index % 4;
            IRow row = sheet.CreateRow(column_index);
            //设置行高 ,excel行高度每个像素点是1/20
       
            row.Height = 60 * 20;
            

            byte[] bytes = Bitmap2Byte(bitmap);
            int pictureIdx = workbook.AddPicture(bytes, PictureType.JPEG);
            HSSFPatriarch patriarch = (HSSFPatriarch)sheet.CreateDrawingPatriarch();
            // 插图片的位置  HSSFClientAnchor（dx1,dy1,dx2,dy2,col1,row1,col2,row2) 后面再作解释
            HSSFClientAnchor anchor = new HSSFClientAnchor(70, 10, 0, 0, rowline, column_index, rowline+1, column_index + 1);
            //把图片插到相应的位置
            HSSFPicture pict = (HSSFPicture)patriarch.CreatePicture(anchor, pictureIdx);
                    
            
        }

    
        public static byte[] Bitmap2Byte(Bitmap bitmap)
        {
            MemoryStream ms = new MemoryStream();
            bitmap.Save(ms, System.Drawing.Imaging.ImageFormat.Bmp);
            byte[] bytes = ms.GetBuffer();  //byte[]   bytes=   ms.ToArray(); 这两句都可以，至于区别么，下面有解释
            ms.Close();
            return bytes;
        }

        private void button_Click(object sender, RoutedEventArgs e)
        {
            //创建一个工作簿
            workbook = new HSSFWorkbook();
            //创建一个sheet
            sheet = workbook.CreateSheet("sheet");
            //sheet.DefaultColumnWidth=30 * 256;
            //sheet.DefaultRowHeight=40 * 20;
            // 设置列宽,excel列宽每个像素是1/256
            sheet.SetColumnWidth(0, 32 * 256);
            sheet.SetColumnWidth(1, 32* 256);
            sheet.SetColumnWidth(2, 32 * 256);
            sheet.SetColumnWidth(3, 32 * 256);
            
            for (int i = 0; i < 100; i++)
            {
                 GenerateBarcode((1000000+i).ToString(), 512, 150,i);
                GC.Collect();
            }
            //image1.Source = GenerateBarcode("128956", 512, 150);


            using (Stream stream = File.OpenWrite("D:\\a.xls"))
            {
                workbook.Write(stream);
            }
        }

        private BitmapImage BitmapToBitmapImage(System.Drawing.Bitmap bitmap)
        {
            BitmapImage bitmapImage = new BitmapImage();
            using (System.IO.MemoryStream ms = new System.IO.MemoryStream())
            {
                bitmap.Save(ms, bitmap.RawFormat);
                bitmapImage.BeginInit();
                bitmapImage.StreamSource = ms;
                bitmapImage.CacheOption = BitmapCacheOption.OnLoad;
                bitmapImage.EndInit();
                bitmapImage.Freeze();
            }
            return bitmapImage;
        }

        public string AddSpace(string text, int spacingIndex)
        {
            StringBuilder sb = new StringBuilder(text);
            for (int i = spacingIndex; i <= sb.Length; i += spacingIndex + 2)
            {
                sb.Insert(i, "  ");
            }
            return sb.ToString();
        }
    }
}
