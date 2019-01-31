__package.require("android.graphics.Paint").as("Paint");
__reflect.from(__package)

var mView

/**
 * 绘图
 **/
function onDraw(canvas) {
    if(mView == null) {
        mView = __context.findViewById("#view");
    }
    var mPaint = __reflect.on("Paint").c_().new_();
    mPaint.setColor(__color.parseColor("#cc0000"));
    mPaint.setStyle(__reflect.clear().on("Paint$Style").get("FILL"));
    canvas.drawCircle(mView.getWidth() / 2, mView.getHeight() / 2, 100, mPaint);
    //__console.toast(mPaint)
}