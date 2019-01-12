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
using System.Windows.Interop;
using System.Configuration;
using System.Xml;
using System.Diagnostics;


using System.Data.SQLite;
using System.Data;
using System.Data.Common;

namespace DeviceManager
{
    /// <summary>
    /// MainWindow.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {

        public HSSFWorkbook workbook;
        ISheet sheet;
        string save_path =".";
        long code = 100000;
        XmlDocument doc;
        public MainWindow()
        {
            InitializeComponent();
            SourceInitialized += HandleInitialized;

            doc = new XmlDocument();


            doc.Load("./config.xml");

            // 3.读取你指定的节点

            XmlNodeList lis = doc.GetElementsByTagName("savepath");
            save_path = lis[0].InnerText;
            path_text.Text = "生成文件夹：" + save_path;

            XmlNodeList lis2 = doc.GetElementsByTagName("code");
            code = long.Parse(lis2[0].InnerText);
          
            UpdateRange(0);

            process_bar.Visibility = Visibility.Hidden;
        }
        protected  void onClosing(ExitEventArgs e)
        { 
            doc.Save("./config.xml"); //????
        }

        public void HandleInitialized(object o, EventArgs e)
        {
            IntPtr wptr = new WindowInteropHelper(this).Handle;
            HwndSource hs = HwndSource.FromHwnd(wptr);
            hs.AddHook(new HwndSourceHook(WpfHandleWinowMsg));
        }

        public IntPtr WpfHandleWinowMsg(IntPtr hwnd, int msg, IntPtr wParam, IntPtr lParam, ref bool handled)
        {
            //这个函数可以做很多事情，只要是windows消息都会经过，例如注册全局快捷键，修改窗体大小边框，等等
            //也可以调API做对应的事情
            switch (msg)
            {
                case 1:
                    break;
            }
            //handled = true;
            return IntPtr.Zero;
        }
        //[DllImport("user32.dll", CharSet = CharSet.Auto)]
        //static extern IntPtr SendMessage(IntPtr hWnd, UInt32 Msg, IntPtr wParam, StringBuilder lParam);



        public void UpdateRange(int add) {
            //add 为0 则显示 大于0  更新文件
            XmlNodeList lis2 = doc.GetElementsByTagName("code");
            code = code + add;
            lis2[0].InnerText=code .ToString();
            range_text.Text = "生成标号区间："+ code + " - "+ (code +100);

            doc.Save("./config.xml");
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

        public string AddSpace(string text, int spacingIndex)
        {
            StringBuilder sb = new StringBuilder(text);
            for (int i = spacingIndex; i <= sb.Length; i += spacingIndex + 2)
            {
                sb.Insert(i, "  ");
            }
            return sb.ToString();
        }


        public void ChoosePath(object sender, RoutedEventArgs e)
        {
            Gat.Controls.OpenDialogView openDialog = new Gat.Controls.OpenDialogView();
            Gat.Controls.OpenDialogViewModel vm = (Gat.Controls.OpenDialogViewModel)openDialog.DataContext;
            vm.IsDirectoryChooser = true;
           
            bool? result = vm.Show();
            string path;
            if (result == true)
            {
                // Get selected file path
                path = vm.SelectedFilePath;
                if (path == null) return;
                path_text.Text = "生成文件夹："+path;
                XmlNodeList lis = doc.GetElementsByTagName("savepath");
                lis[0].InnerText= path;
                doc.Save("./config.xml");
                save_path = path;
            }

           
        }

        private void button_Click(object sender, RoutedEventArgs e)
        {

            //Window win = new Window();
            //win.Show();

            
            process_bar.Maximum = 100;
            process_bar.Value = 0;

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
                 GenerateBarcode((code+i).ToString(), 512, 150,i);
                process_bar.Value = i;
                //this.UpdateLayout();

                GC.Collect();
            }
            //image1.Source = GenerateBarcode("128956", 512, 150);


            using (Stream stream = File.OpenWrite(save_path + "\\"+ code + ".xls"))
            {
                workbook.Write(stream);
            }
            
            //定义消息框             
            string messageBoxText = "生成完成   "+ code + ".xls";
            string caption = "消息";
            MessageBoxButton button = MessageBoxButton.OK;
            MessageBoxImage icon = MessageBoxImage.None;
            //显示消息框              
            MessageBoxResult result = MessageBox.Show(messageBoxText, caption, button, icon);
            UpdateRange(100);
            Process.Start(save_path);
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////
        protected bool isNumberic(string message, out int result)
        {
            //判断是否为整数字符串
            //是的话则将其转换为数字并将其设为out类型的输出值、返回true, 否则为false
            result = -1;   //result 定义为out 用来输出值
            try
            {
                //当数字字符串的为是少于4时，以下三种都可以转换，任选一种
                //如果位数超过4的话，请选用Convert.ToInt32() 和int.Parse()

                //result = int.Parse(message);
                //result = Convert.ToInt16(message);
                result = Convert.ToInt32(message);
                return true;
            }
            catch
            {
                return false;
            }
        }

        public BitmapSource GenerateOneBarcode(string text, int width, int height, int index)
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


            // genExcel(bitmap, index);


            IntPtr ip = bitmap.GetHbitmap();//从GDI+ Bitmap创建GDI位图对象
                                            //Imaging.CreateBitmapSourceFromHBitmap方法，基于所提供的非托管位图和调色板信息的指针，返回一个托管的BitmapSource
            BitmapSource bitmapSource = System.Windows.Interop.Imaging.CreateBitmapSourceFromHBitmap(ip, IntPtr.Zero, Int32Rect.Empty,
            System.Windows.Media.Imaging.BitmapSizeOptions.FromEmptyOptions());
            // DeleteObject(ip);
            return bitmapSource;
        }
        private void GenOne(object sender, RoutedEventArgs e)
        {
            bool state = true;
            string messageBoxText = " ";
            int num;
            if (!isNumberic(gen_number.Text,out num) )
            {
                state = false;
                messageBoxText = "必须是6位数字";
            }

            if (gen_number.Text.Length != 6)
            {
                state = false;
                messageBoxText = "必须是6位数字";
            }

            if (!state)
            {
                //定义消息框             
                string caption = "消息";
                MessageBoxButton button = MessageBoxButton.OK;
                MessageBoxImage icon = MessageBoxImage.None;
                //显示消息框              
                MessageBoxResult result = MessageBox.Show(messageBoxText, caption, button, icon);
                return;

            }
           image1.Source= GenerateOneBarcode(gen_number.Text, 512, 150, 0);
        }




        private void ImportDatabase(object sender, RoutedEventArgs e)
        {

        }


    }
}
