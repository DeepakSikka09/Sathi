package in.ecomexpress.sathi.utils.signature_pad.utils;

public class SvgBuilder {

    private final StringBuilder mSvgPathsBuilder = new StringBuilder();
    private SvgPathBuilder mCurrentPathBuilder = null;

    public SvgBuilder() {}

    public void clear() {
        mSvgPathsBuilder.setLength(0);
        mCurrentPathBuilder = null;
    }

    public String build(final int width, final int height) {
        if (isPathStarted()) {
            appendCurrentPath();
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.2\" baseProfile=\"tiny\" " +
                "height=\"" +
                height +
                "\" " +
                "width=\"" +
                width +
                "\" " +
                "viewBox=\"" +
                0 +
                " " +
                0 +
                " " +
                width +
                " " +
                height +
                "\">" +
                "<g " +
                "stroke-linejoin=\"round\" " +
                "stroke-linecap=\"round\" " +
                "fill=\"none\" " +
                "stroke=\"black\"" +
                ">" +
                mSvgPathsBuilder +
                "</g>" +
                "</svg>";
    }

    public SvgBuilder append(final Bezier curve, final float strokeWidth) {
        final Integer roundedStrokeWidth = Math.round(strokeWidth);
        final SvgPoint curveStartSvgPoint = new SvgPoint(curve.startPoint);
        final SvgPoint curveControlSvgPoint1 = new SvgPoint(curve.control1);
        final SvgPoint curveControlSvgPoint2 = new SvgPoint(curve.control2);
        final SvgPoint curveEndSvgPoint = new SvgPoint(curve.endPoint);

        if (!isPathStarted()) {
            startNewPath(roundedStrokeWidth, curveStartSvgPoint);
        }

        if (!curveStartSvgPoint.equals(mCurrentPathBuilder.getLastPoint())
                || !roundedStrokeWidth.equals(mCurrentPathBuilder.getStrokeWidth())) {
            appendCurrentPath();
            startNewPath(roundedStrokeWidth, curveStartSvgPoint);
        }

        mCurrentPathBuilder.append(curveControlSvgPoint1, curveControlSvgPoint2, curveEndSvgPoint);
        return this;
    }

    private void startNewPath(Integer roundedStrokeWidth, SvgPoint curveStartSvgPoint) {
        mCurrentPathBuilder = new SvgPathBuilder(curveStartSvgPoint, roundedStrokeWidth);
    }

    private void appendCurrentPath() {
        mSvgPathsBuilder.append(mCurrentPathBuilder);
    }

    private boolean isPathStarted() {
        return mCurrentPathBuilder != null;
    }
}