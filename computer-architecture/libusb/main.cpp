#include <iostream>
#include <libusb-1.0/libusb.h>
#include <stdio.h>

using namespace std;

void printdev(libusb_device *dev)
{
    libusb_device_descriptor descr;
    libusb_device_handle *handle;

    int r = libusb_get_device_descriptor(dev, &descr);
    if (r < 0)
        exit(1);

    r = libusb_open(dev, &handle);
    if (r < 0)
    {
        printf("An error occured opening device\n");
        exit(1);
    }

    printf("DeviceClass %.2d; VendorId %.2d; ProductId %.2d; \n",
           (int) descr.bDeviceClass, descr.idVendor, descr.idProduct);

    unsigned char data[256] = {0};
    if (!descr.iSerialNumber) {
        r = libusb_get_string_descriptor_ascii(handle, descr.iSerialNumber, data, 256);
        if (r < 0) {
            printf("Descriptor serial slomalsya\n");
        }
        cout << data << endl;
    }

    if (descr.iProduct) {
        r = libusb_get_string_descriptor_ascii(handle, descr.iProduct, data, 256);
        if (r < 0) {
            printf("Descriptor product slomalsya\n");
        }
        cout << data << endl;
    }

    if (!descr.iManufacturer) {
        r = libusb_get_string_descriptor_ascii(handle, descr.iManufacturer, data, 256);
        if (r < 0) {
            printf("Descriptor manufacturer slomalsya\n");
        }
        cout << data << endl;
    }

    cout << "---------------------------------" << endl;
    libusb_close(handle);
}

int main() {
    libusb_device **devices;
    ssize_t cnt;
    libusb_context *ctx = NULL;
    int r = libusb_init(&ctx);
    if (r < 0)
        exit(1);

    cnt = libusb_get_device_list(ctx, &devices);
    if (cnt < 0)
        exit(1);

    for (int i = 0; i < cnt; i++)
    {
        printdev(devices[i]);
    }


    libusb_free_device_list(devices, 1);
    libusb_exit(ctx);
    return 0;
}
