import { cn } from "@/lib/utils";

type BadgeProps = {
  id: string,
  label: string,
  color?: string,
  className?: string,
  muted?: boolean
}

const Badge = ({ id, label, color = "", className = "", muted = false}: BadgeProps): React.ReactElement => {
  const transition = "transition-colors duration-200 ease-in-out";
  
  if(muted) {
    return (
      <div
        id={id}
        className={cn(
          "cursor-default border border-white/30",
          transition
        )}
      >
        <span className="text-[12px] px-2 py-1 font-bold text-white/90 hover:text-white">{label}</span>
      </div>
    );
  }
  
  return (
    <div
      id={id}
      className={cn(
        `cursor-default uppercase border border-${color} text-${color} hover:bg-${color} hover:text-white`,
        transition,
        className
      )}
    >
      <span className="text-[12px] px-2 py-1 font-bold">{ label }</span>
    </div>
  );
}

export { Badge };