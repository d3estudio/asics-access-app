package co.ferreri.asicsaccess;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;

import java.io.IOException;

public class PrinterHelper {
    private Printer printer;
    private Context context;
    private String printerIpAddress = "";

    public PrinterHelper(Context context) {
        this.context = context;
        this.printer = new Printer();
        findPrinters();
    }

    public void findPrinters() {

        new Thread() {
            public void run() {
                Looper.prepare();

                try {

                    NetPrinter[] mNetPrinter = printer.getNetPrinters("QL-720NW");
                    final int netPrinterCount = mNetPrinter.length;

                    if (netPrinterCount > 0) {
                        String dispBuff[] = new String[netPrinterCount];
                        dispBuff[0] = mNetPrinter[0].modelName + "\n\n"
                                + mNetPrinter[0].ipAddress + "\n"
                                + mNetPrinter[0].macAddress + "\n"
                                + mNetPrinter[0].serNo + "\n"
                                + mNetPrinter[0].nodeName;
                        printerIpAddress = mNetPrinter[0].ipAddress;
                        Log.e("PRINTER", printerIpAddress);
                        Utils.showCenteredToast(context, "Printer connected IP "+printerIpAddress, 1);

                    } else { // when no printer
                        Utils.showCenteredToast(context, "No printer found to connect", 1);
                        Log.e("PRINTER", "NONE FOUND");
                    }

                } catch (Exception e) {
                    Log.e("PRINTER", "PRINT CONNECT EXCEPTION" + e);
                }

                Looper.loop();
            }
        }.start();

    }

    public void print(final Guest guest) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();

                try {

                    String[] splitedName = guest.getName().split("\\s+");

                    int firstName = (int) Math.ceil((double)splitedName.length / 2);
                    int lastName = (int) Math.floor((double)splitedName.length / 2);

                    Log.e("PRINT", "NAME LENGTH fisrt " + firstName +" last "+lastName);

//                    PrinterInfo printInfo = new PrinterInfo();
//                    printInfo.printerModel = PrinterInfo.Model.QL_720NW;
//                    printInfo.port = PrinterInfo.Port.NET;
//                    printInfo.ipAddress = printerIpAddress;
//                    printInfo.labelNameIndex = LabelInfo.QL700.W29H90.ordinal();
//
//                    printer.setPrinterInfo(printInfo);
//
//                    Boolean val = printer.startPTTPrint(5, null);
//                    Log.e("print", "startPTTPrint >>>> " + val);
//
//                    // Replace text
//                    printer.replaceText("Junhao;Magalha;"+guest.getQrCode());
//
//                    // Transmit P-touc Template command print data
//                    PrinterStatus status = printer.flushPTTPrint();
//
//                    if (status.errorCode != PrinterInfo.ErrorCode.ERROR_NONE) {
//                        Utils.showCenteredToast(context, "PRINTER: "+status.errorCode, 1);
//                        findPrinters();
//                    }
//
//                    Log.e("print", "PrinterStatus  err >>>> " + status.errorCode);
                }catch (Exception e){
                    Log.e("PRINT", "PRINT EXCEPTION " + e);
                }

                Looper.loop();

            }
        }).start();
    }
}
