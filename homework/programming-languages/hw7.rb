class GeometryExpression  
  Epsilon = 0.00001
end

class GeometryValue   
  private  
  def real_close(r1,r2) 
      (r1 - r2).abs < GeometryExpression::Epsilon
  end
  def real_close_point(x1,y1,x2,y2) 
      real_close(x1,x2) && real_close(y1,y2)
  end  
  def two_points_to_line(x1,y1,x2,y2) 
      if real_close(x1,x2)
        VerticalLine.new x1
      else
        m = (y2 - y1).to_f / (x2 - x1)
        b = y1 - m * x1
        Line.new(m,b)
      end
  end

  public
  def intersectNoPoints np
    np # could also have NoPoints.new here instead
  end

  def intersectLineSegment seg
    line_result = intersect(two_points_to_line(seg.x1,seg.y1,seg.x2,seg.y2))
    line_result.intersectWithSegmentAsLineResult seg
  end
end

class NoPoints < GeometryValue
  def eval_prog env 
    self # all values evaluate to self
  end
  def preprocess_prog
    self # no pre-processing to do here
  end
  def shift(dx,dy)
    self # shifting no-points is no-points
  end
  def intersect other
    other.intersectNoPoints self # will be NoPoints but follow double-dispatch
  end
  def intersectPoint p
    self # intersection with point and no-points is no-points
  end
  def intersectLine line
    self # intersection with line and no-points is no-points
  end
  def intersectVerticalLine vline
    self # intersection with line and no-points is no-points
  end  
  def intersectWithSegmentAsLineResult seg
    self
  end
end


class Point < GeometryValue  
  attr_reader :x, :y
  def initialize(x,y)
    @x = x
    @y = y
  end  
  def preprocess_prog
    self
  end
  def eval_prog env
    self
  end
  def shift(dx,dy)
    Point.new(x+dx,y+dy)
  end
  def intersect other
    other.intersectPoint self
  end
  def intersectPoint p
    if real_close_point(x,y,p.x,p.y)
    then p
    else NoPoints.new
    end
  end
  def intersectLine line
    if real_close(y,x * line.m + line.b)
    then self
    else NoPoints.new
    end
  end
  def intersectVerticalLine vline
    if real_close(x, vline.x)
    then self
    else NoPoints.new
    end
  end
  def intersectWithSegmentAsLineResult seg
    if inbetween(x,seg.x1,seg.x2) and inbetween(y,seg.y1,seg.y2)
    then self
    else NoPoints.new
    end
  end

  private
  def inbetween(v,end1,end2)
    (end1-GeometryExpression::Epsilon <= v and v <= end2 + GeometryExpression::Epsilon) or
      (end2-GeometryExpression::Epsilon <= v and v <= end1+GeometryExpression::Epsilon)
  end
end

class Line < GeometryValue  
  attr_reader :m, :b 
  def initialize(m,b)
    @m = m
    @b = b
  end
  def preprocess_prog
    self
  end
  def eval_prog env
    self
  end
  def shift(dx,dy)
    Line.new(m,b+dy-m*dx)
  end  
  def intersect other
    other.intersectLine self
  end
  def intersectPoint p
    p.intersectLine self
  end
  def intersectLine line
    if real_close(m,line.m)
    then if real_close(b,line.b)
         then line
         else NoPoints.new
         end
    else Point.new((line.b-b)/(m-line.m),m*(line.b-b)/(m-line.m)+b)
    end
  end
  def intersectVerticalLine vline
    Point.new(vline.x, m*vline.x+b)
  end
  def intersectWithSegmentAsLineResult seg
    seg
  end
end

class VerticalLine < GeometryValue  
  attr_reader :x
  def initialize x
    @x = x
  end
  def preprocess_prog
    self
  end
  def eval_prog env
    self
  end
  def shift(dx,dy)
    VerticalLine.new(x+dx)
  end
  def intersect other
    other.intersectVerticalLine self
  end
  def intersectPoint p
    p.intersectVerticalLine self
  end
  def intersectLine line
    line.intersectVerticalLine self
  end
  def intersectVerticalLine vline
    if real_close(x, vline.x)
    then vline
    else NoPoints.new
    end
  end
  def intersectWithSegmentAsLineResult seg
    seg
  end
end

class LineSegment < GeometryValue  
  attr_reader :x1, :y1, :x2, :y2
  def initialize (x1,y1,x2,y2)
    @x1 = x1
    @y1 = y1
    @x2 = x2
    @y2 = y2
  end
  def preprocess_prog
    if x1 == x2 and y1 == y2
    then Point.new(x1, y1)
    else if x1 > x2
         then LineSegment.new(x2,y2,x1,y1)
         else self
         end
    end
  end
  def eval_prog env
    self
  end
  def shift(dx,dy)
    LineSegment.new(x1+dx,y1+dy,x2+dx,y2+dy)
  end

  def intersect other
    other.intersectLineSegment self
  end
  def intersectPoint p
    p.intersectLineSegment self
  end
  def intersectLine line
    line.intersectLineSegment self
  end
  def intersectVerticalLine vline
    vline.intersectLineSegment self
  end
  def intersectWithSegmentAsLineResult seg
    seg1=[x1,y1,x2,y2]
    seg2=[seg.x1,seg.y1,seg.x2,seg.y2]
    if real_close(x1,x2)
    then aXstart,aYstart,aXend,aYend,bXstart,bYstart,bXend,bYend=
        y1 < seg.y1 ? seg1+seg2 : seg2+seg1
      if real_close(aYend, bYstart)
      then Point.new(aXend, aYend)
      else if aYend < bYstart
           then NoPoints.new
           else if aYend > bYend
                then LineSegment.new(bXstart,bYstart,bXend,bYend)
                else LineSegment.new(bXstart,bYstart,aXend,aYend)
                end
           end
      end
    else aXstart,aYstart,aXend,aYend,bXstart,bYstart,bXend,bYend=
        x1 < seg.x1 ? seg1+seg2 : seg2+seg1
      if real_close(aXend,bXstart)
      then Point.new(aXend,aYend)
      else if aXend < bXstart
           then NoPoints.new
             else if aXend > bXend
                  then LineSegment.new(bXstart,bYstart,bXend,bYend)
                  else LineSegment.new(bXstart,bYstart,aXend,aYend)
                  end
           end
      end
    end
  end 
end

class Intersect < GeometryExpression  
  def initialize(e1,e2)
    @e1 = e1
    @e2 = e2
  end
  def preprocess_prog
    Intersect.new(@e1.preprocess_prog,@e2.preprocess_prog)
  end
  def eval_prog env
    @e1.eval_prog(env).intersect(@e2.eval_prog(env))
  end
end

class Let < GeometryExpression
  def initialize(s,e1,e2)
    @s = s
    @e1 = e1
    @e2 = e2
  end
  def preprocess_prog
    Let.new(@s,@e1.preprocess_prog,@e2.preprocess_prog)
  end
  def eval_prog env
    # @e2.eval_prog(env.push([@s,@e1.eval_prog(env)]))
    @e2.eval_prog([[@s, @e1.eval_prog(env)]] + env)
  end
end

class Var < GeometryExpression  
  def initialize s
    @s = s
  end
  def eval_prog env
    pr = env.assoc @s
    raise "undefined variable" if pr.nil?
    pr[1]
  end
  def preprocess_prog
    self
  end
end

class Shift < GeometryExpression  
  def initialize(dx,dy,e)
    @dx = dx
    @dy = dy
    @e = e
  end
  def preprocess_prog
    Shift.new(@dx,@dy,@e.preprocess_prog)
  end
  def eval_prog env
    @e.eval_prog(env).shift(@dx,@dy)
  end
end
