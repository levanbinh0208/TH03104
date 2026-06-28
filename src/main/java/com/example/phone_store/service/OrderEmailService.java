package com.example.phone_store.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import com.example.phone_store.entity.Order;
import com.example.phone_store.entity.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class OrderEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendOrderConfirmation(String toEmail, Order order, List<OrderDetail> details) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("📱 [PhoneStore] Xác nhận đơn hàng #" + order.getOrderId());
            helper.setText(buildHtml(order, details), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("[OrderEmailService] Gửi email thất bại: " + e.getMessage());
        }
    }

    private String buildHtml(Order order, List<OrderDetail> details) {
        DecimalFormat df = new DecimalFormat("#,###");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy");

        String orderDate = order.getOrderDate() != null
                ? sdf.format(order.getOrderDate()) : "—";

        String paymentLabel = switch (order.getPaymentMethod() != null
                ? order.getPaymentMethod() : "") {
            case "COD"           -> "💵 Tiền mặt khi nhận hàng";
            case "BANK_TRANSFER" -> "🏦 Chuyển khoản ngân hàng";
            case "MOMO"          -> "📱 Ví MoMo";
            default              -> order.getPaymentMethod();
        };

        StringBuilder rows = new StringBuilder();
        long grandTotal = 0;
        int idx = 1;
        for (OrderDetail d : details) {
            long subtotal = d.getPrice() * d.getQuantity();
            grandTotal += subtotal;

            rows.append("""
                <tr style="border-bottom:1px solid #f1f5f9;">
                  <td style="padding:12px 8px;color:#475569;">%d</td>
                  <td style="padding:12px 8px;font-weight:600;color:#1e293b;">%s</td>
                  <td style="padding:12px 8px;text-align:center;color:#475569;">x%d</td>
                  <td style="padding:12px 8px;text-align:right;color:#475569;">%s VNĐ</td>
                  <td style="padding:12px 8px;text-align:right;font-weight:700;color:#1e293b;">%s VNĐ</td>
                </tr>
                """.formatted(
                    idx++,
                    d.getProduct() != null ? d.getProduct().getProductName() : "Sản phẩm",
                    d.getQuantity(),
                    df.format(d.getPrice()),
                    df.format(subtotal)
            ));
        }

        return """
        <!DOCTYPE html>
        <html lang="vi">
        <head><meta charset="UTF-8"></head>
        <body style="margin:0;padding:0;background:#f0f4f8;font-family:'Segoe UI',Arial,sans-serif;">

        <table width="100%%" cellpadding="0" cellspacing="0"
               style="background:#f0f4f8;padding:30px 0;">
          <tr><td align="center">
            <table width="600" cellpadding="0" cellspacing="0"
                   style="background:#ffffff;border-radius:16px;overflow:hidden;
                          box-shadow:0 4px 24px rgba(0,0,0,0.08);max-width:100%%;">

              <!-- HEADER -->
              <tr>
                <td style="background:linear-gradient(135deg,#1d4ed8,#3b82f6);
                           padding:32px 40px;text-align:center;">
                  <p style="margin:0;font-size:32px;">📱</p>
                  <h1 style="margin:8px 0 4px;color:#fff;font-size:24px;
                             letter-spacing:1px;">PhoneStore</h1>
                  <p style="margin:0;color:#bfdbfe;font-size:14px;">
                    Cảm ơn bạn đã tin tưởng mua sắm!
                  </p>
                </td>
              </tr>

              <tr>
                <td style="background:#ecfdf5;padding:20px 40px;text-align:center;
                           border-bottom:1px solid #d1fae5;">
                  <p style="margin:0;font-size:20px;font-weight:700;color:#065f46;">
                    ✅ Đặt hàng thành công!
                  </p>
                  <p style="margin:6px 0 0;color:#047857;font-size:14px;">
                    Đơn hàng của bạn đã được ghi nhận và đang chờ xác nhận.
                  </p>
                </td>
              </tr>

              <!-- THÔNG TIN ĐƠN HÀNG -->
              <tr>
                <td style="padding:28px 40px 0;">
                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td style="width:50%%;vertical-align:top;">
                        <p style="margin:0 0 4px;font-size:12px;color:#94a3b8;
                                  text-transform:uppercase;letter-spacing:.5px;">Mã đơn hàng</p>
                        <p style="margin:0;font-size:22px;font-weight:800;color:#1d4ed8;">
                          #%s
                        </p>
                      </td>
                      <td style="width:50%%;vertical-align:top;text-align:right;">
                        <p style="margin:0 0 4px;font-size:12px;color:#94a3b8;
                                  text-transform:uppercase;letter-spacing:.5px;">Ngày đặt</p>
                        <p style="margin:0;font-size:14px;color:#334155;font-weight:600;">
                          %s
                        </p>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>

              <tr>
                <td style="padding:20px 40px 0;">
                  <table width="100%%" cellpadding="0" cellspacing="0"
                         style="background:#f8fafc;border-radius:12px;padding:20px;
                                border:1px solid #e2e8f0;">
                    <tr>
                      <td style="padding-bottom:10px;" colspan="2">
                        <p style="margin:0;font-size:13px;font-weight:700;color:#475569;
                                  text-transform:uppercase;letter-spacing:.5px;">
                          📋 Thông tin giao hàng
                        </p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:4px 0;font-size:13px;color:#94a3b8;width:40%%;">
                        👤 Người nhận
                      </td>
                      <td style="padding:4px 0;font-size:13px;font-weight:600;color:#1e293b;">
                        %s
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:4px 0;font-size:13px;color:#94a3b8;">
                        📞 Điện thoại
                      </td>
                      <td style="padding:4px 0;font-size:13px;font-weight:600;color:#1e293b;">
                        %s
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:4px 0;font-size:13px;color:#94a3b8;vertical-align:top;">
                        📍 Địa chỉ
                      </td>
                      <td style="padding:4px 0;font-size:13px;font-weight:600;color:#1e293b;">
                        %s
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:4px 0;font-size:13px;color:#94a3b8;">
                        💳 Thanh toán
                      </td>
                      <td style="padding:4px 0;font-size:13px;font-weight:600;color:#1e293b;">
                        %s
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>

              <tr>
                <td style="padding:20px 40px 0;">
                  <p style="margin:0 0 12px;font-size:13px;font-weight:700;color:#475569;
                            text-transform:uppercase;letter-spacing:.5px;">
                    🧾 Hoá đơn chi tiết
                  </p>
                  <table width="100%%" cellpadding="0" cellspacing="0"
                         style="border-radius:12px;overflow:hidden;
                                border:1px solid #e2e8f0;font-size:13px;">
                    <thead>
                      <tr style="background:#1d4ed8;color:#fff;">
                        <th style="padding:10px 8px;text-align:left;font-weight:600;">#</th>
                        <th style="padding:10px 8px;text-align:left;font-weight:600;">Sản phẩm</th>
                        <th style="padding:10px 8px;text-align:center;font-weight:600;">SL</th>
                        <th style="padding:10px 8px;text-align:right;font-weight:600;">Đơn giá</th>
                        <th style="padding:10px 8px;text-align:right;font-weight:600;">Thành tiền</th>
                      </tr>
                    </thead>
                    <tbody>
                      %s
                    </tbody>
                  </table>
                </td>
              </tr>

              <tr>
                <td style="padding:0 40px 28px;">
                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td style="padding-top:12px;">
                        <table width="100%%" cellpadding="0" cellspacing="0">
                          <tr>
                            <td style="font-size:13px;color:#64748b;">Tạm tính:</td>
                            <td style="font-size:13px;color:#64748b;text-align:right;">
                              %s VNĐ
                            </td>
                          </tr>
                          <tr>
                            <td style="font-size:13px;color:#64748b;padding-top:4px;">
                              Phí vận chuyển:
                            </td>
                            <td style="font-size:13px;color:#16a34a;
                                       text-align:right;padding-top:4px;font-weight:600;">
                              Miễn phí
                            </td>
                          </tr>
                          <tr>
                            <td colspan="2">
                              <hr style="border:none;border-top:2px solid #e2e8f0;
                                         margin:12px 0;">
                            </td>
                          </tr>
                          <tr>
                            <td style="font-size:16px;font-weight:800;color:#1e293b;">
                              TỔNG CỘNG:
                            </td>
                            <td style="font-size:20px;font-weight:800;color:#e11d48;
                                       text-align:right;">
                              %s VNĐ
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>

              <tr>
                <td style="padding:0 40px 28px;">
                  <table width="100%%" cellpadding="0" cellspacing="0"
                         style="background:#fffbeb;border-radius:12px;
                                padding:16px 20px;border:1px solid #fde68a;">
                    <tr>
                      <td>
                        <p style="margin:0;font-size:13px;color:#92400e;font-weight:700;">
                          ⏳ Trạng thái: Chờ xác nhận
                        </p>
                        <p style="margin:6px 0 0;font-size:13px;color:#78350f;">
                          Chúng tôi sẽ liên hệ qua số <strong>%s</strong>
                          để xác nhận và thông báo lịch giao hàng.
                        </p>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>

              <tr>
                <td style="background:#1e293b;padding:24px 40px;text-align:center;">
                  <p style="margin:0;color:#94a3b8;font-size:13px;">
                    📱 <strong style="color:#e2e8f0;">PhoneStore</strong> —
                    Điện thoại chính hãng, giá tốt nhất
                  </p>
                  <p style="margin:8px 0 0;color:#64748b;font-size:12px;">
                    Email này được gửi tự động, vui lòng không phản hồi trực tiếp.
                  </p>
                </td>
              </tr>

            </table>
          </td></tr>
        </table>

        </body>
        </html>
        """.formatted(
                order.getOrderId(),           // mã đơn
                orderDate,                    // ngày đặt
                order.getReceiverName(),      // người nhận
                order.getPhone(),             // điện thoại
                order.getShippingAddress(),   // địa chỉ
                paymentLabel,                 // thanh toán
                rows,                         // dòng sản phẩm
                df.format(grandTotal),        // tạm tính
                df.format(grandTotal),        // tổng cộng
                order.getPhone()              // số điện thoại trong note
        );
    }
}